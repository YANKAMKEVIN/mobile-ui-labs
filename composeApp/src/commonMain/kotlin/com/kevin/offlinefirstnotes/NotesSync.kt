package com.kevin.offlinefirstnotes

/**
 * The note and its sync queue as a pure reducer. Every edit is written on the device first and
 * queued; a push sends the queue to a (simulated) server, which rejects it when another device
 * changed the note since the last sync. Nothing is persisted: the lab shows the states, not a
 * storage layer.
 */
internal enum class SyncMode { Online, Offline, Queue, Syncing, Conflict }

internal data class NotesState(
    val online: Boolean = true,
    /** The note as written on this device. */
    val local: String = NOTE_START,
    /** The note as the server holds it. */
    val remote: String = NOTE_START,
    /** The server version this device last synced with. */
    val base: String = NOTE_START,
    /** Edits written locally and not yet on the server, oldest first. */
    val pending: List<String> = emptyList(),
    /** A push is on its way to the server. */
    val syncing: Boolean = false,
    /** Both sides changed since [base]: the push stops until the user picks a version. */
    val conflict: Conflict? = null
) {
    val mode: SyncMode
        get() = when {
            conflict != null -> SyncMode.Conflict
            syncing -> SyncMode.Syncing
            !online -> SyncMode.Offline
            pending.isNotEmpty() -> SyncMode.Queue
            else -> SyncMode.Online
        }

    /** "Sync now" makes sense: online, something queued, nothing in the way. */
    val canSync: Boolean get() = online && pending.isNotEmpty() && !syncing && conflict == null
}

internal data class Conflict(val local: String, val remote: String)

internal enum class Resolution { KeepMine, KeepTheirs, KeepBoth }

internal sealed interface NotesAction {
    /** The user adds [line] to the note: saved on the device at once, queued for the server. */
    data class Write(val line: String) : NotesAction
    data object GoOffline : NotesAction
    data object GoOnline : NotesAction
    data object SyncNow : NotesAction
    /** The simulated server answers the push in flight. */
    data object PushFinished : NotesAction
    /** Another device saves [line] in the same note, straight to the server. */
    data class RemoteWrite(val line: String) : NotesAction
    data class Resolve(val resolution: Resolution) : NotesAction
    /** Demo shortcut from the tabs: a ready-made state for [mode]. */
    data class JumpTo(val mode: SyncMode) : NotesAction
}

internal const val NOTE_START = "Museum list, quiet cafe, evening walk near the river."

/** Lines the demo adds, in turn, when the user writes on this device or on the other one. */
internal val LOCAL_LINES = listOf("Add receipts later.", "Book the cafe for 7 pm.", "Pack the rain jacket.")
internal val REMOTE_LINES = listOf("Train back at 18:40.", "Ask Sam about the museum pass.")

internal fun appendLine(note: String, line: String): String = if (note.isEmpty()) line else "$note\n$line"

internal fun reduce(state: NotesState, action: NotesAction): NotesState = when (action) {
    is NotesAction.Write -> state.copy(
        local = appendLine(state.local, action.line),
        pending = state.pending + action.line
    )

    // Losing the network interrupts a push; the queue keeps the edits for later.
    NotesAction.GoOffline -> state.copy(online = false, syncing = false)

    NotesAction.GoOnline ->
        if (state.online) state
        else pullIfIdle(state.copy(online = true, syncing = state.pending.isNotEmpty() && state.conflict == null))

    NotesAction.SyncNow -> if (state.canSync) state.copy(syncing = true) else state

    NotesAction.PushFinished -> when {
        !state.syncing -> state
        !state.online -> state.copy(syncing = false)
        state.remote != state.base -> state.copy(
            syncing = false,
            conflict = Conflict(local = state.local, remote = state.remote)
        )
        else -> state.copy(syncing = false, remote = state.local, base = state.local, pending = emptyList())
    }

    is NotesAction.RemoteWrite -> pullIfIdle(state.copy(remote = appendLine(state.remote, action.line)))

    is NotesAction.Resolve -> {
        val conflict = state.conflict
        if (conflict == null) state
        else when (action.resolution) {
            // Mine wins: the server version is acknowledged, then the local note is pushed over it.
            Resolution.KeepMine -> state.copy(
                conflict = null,
                local = conflict.local,
                base = conflict.remote,
                syncing = state.online
            )
            Resolution.KeepTheirs -> state.copy(
                conflict = null,
                local = conflict.remote,
                base = conflict.remote,
                pending = emptyList()
            )
            // Both versions stay in the note; the merge is a new local edit to push.
            Resolution.KeepBoth -> {
                val merged = mergeBoth(conflict)
                state.copy(
                    conflict = null,
                    local = merged,
                    base = conflict.remote,
                    pending = listOf(merged),
                    syncing = state.online
                )
            }
        }
    }

    is NotesAction.JumpTo -> preset(action.mode)
}

/** Nothing queued and online: a newer server version simply replaces the local one. */
private fun pullIfIdle(state: NotesState): NotesState =
    if (state.online && state.pending.isEmpty() && !state.syncing && state.conflict == null && state.remote != state.base) {
        state.copy(local = state.remote, base = state.remote)
    } else {
        state
    }

/** The server version, then the lines only this device has, without repeating shared lines. */
internal fun mergeBoth(conflict: Conflict): String {
    val remoteLines = conflict.remote.lines()
    val extra = conflict.local.lines().filter { it !in remoteLines }
    return (remoteLines + extra).joinToString("\n")
}

/** A ready-made state for each tab, so every mode can be shown without replaying the story. */
internal fun preset(mode: SyncMode): NotesState {
    val queued = listOf(LOCAL_LINES[0], LOCAL_LINES[1])
    val written = queued.fold(NOTE_START, ::appendLine)
    return when (mode) {
        SyncMode.Online -> NotesState()
        SyncMode.Offline -> NotesState(online = false, local = appendLine(NOTE_START, LOCAL_LINES[0]), pending = queued.take(1))
        SyncMode.Queue -> NotesState(local = written, pending = queued)
        SyncMode.Syncing -> NotesState(local = written, pending = queued, syncing = true)
        SyncMode.Conflict -> {
            val remote = appendLine(NOTE_START, REMOTE_LINES[0])
            NotesState(local = written, remote = remote, pending = queued, conflict = Conflict(written, remote))
        }
    }
}
