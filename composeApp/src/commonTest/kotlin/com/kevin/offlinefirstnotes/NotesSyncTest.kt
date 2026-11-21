package com.kevin.offlinefirstnotes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotesSyncTest {
    private fun NotesState.then(vararg actions: NotesAction) = actions.fold(this, ::reduce)

    @Test
    fun writingSavesLocallyAndQueuesEvenOnline() {
        val state = NotesState().then(NotesAction.Write("Add receipts later."))
        assertEquals("$NOTE_START\nAdd receipts later.", state.local)
        assertEquals(NOTE_START, state.remote)
        assertEquals(listOf("Add receipts later."), state.pending)
        assertEquals(SyncMode.Queue, state.mode)
        assertTrue(state.canSync)
    }

    @Test
    fun syncPushesTheQueueAndClearsIt() {
        val state = NotesState().then(NotesAction.Write("A"), NotesAction.Write("B"), NotesAction.SyncNow)
        assertEquals(SyncMode.Syncing, state.mode)
        val done = reduce(state, NotesAction.PushFinished)
        assertEquals(SyncMode.Online, done.mode)
        assertEquals("$NOTE_START\nA\nB", done.remote)
        assertEquals(done.remote, done.base)
        assertTrue(done.pending.isEmpty())
    }

    @Test
    fun offlineKeepsEditingAndSyncsWhenBackOnline() {
        val offline = NotesState().then(NotesAction.GoOffline, NotesAction.Write("A"))
        assertEquals(SyncMode.Offline, offline.mode)
        assertFalse(offline.canSync)
        assertEquals(offline, reduce(offline, NotesAction.SyncNow))
        val back = reduce(offline, NotesAction.GoOnline)
        assertEquals(SyncMode.Syncing, back.mode)
        assertEquals("$NOTE_START\nA", reduce(back, NotesAction.PushFinished).remote)
    }

    @Test
    fun losingTheNetworkMidPushKeepsTheQueue() {
        val state = NotesState().then(NotesAction.Write("A"), NotesAction.SyncNow, NotesAction.GoOffline)
        assertFalse(state.syncing)
        assertEquals(listOf("A"), state.pending)
        assertEquals(NOTE_START, state.remote)
        // A late answer to the interrupted push changes nothing.
        assertEquals(state, reduce(state, NotesAction.PushFinished))
    }

    @Test
    fun remoteEditIsPulledWhenNothingIsQueued() {
        val state = NotesState().then(NotesAction.RemoteWrite("Train back at 18:40."))
        assertEquals("$NOTE_START\nTrain back at 18:40.", state.local)
        assertEquals(state.remote, state.base)
        assertEquals(SyncMode.Online, state.mode)
    }

    @Test
    fun remoteEditWhileOfflineArrivesOnReconnect() {
        val offline = NotesState().then(NotesAction.GoOffline, NotesAction.RemoteWrite("R"))
        assertEquals(NOTE_START, offline.local)
        assertEquals("$NOTE_START\nR", reduce(offline, NotesAction.GoOnline).local)
    }

    @Test
    fun pushOverANewerServerVersionStopsOnAConflict() {
        val state = NotesState().then(
            NotesAction.Write("Mine"),
            NotesAction.RemoteWrite("Theirs"),
            NotesAction.SyncNow,
            NotesAction.PushFinished
        )
        assertEquals(SyncMode.Conflict, state.mode)
        val conflict = assertNotNull(state.conflict)
        assertEquals("$NOTE_START\nMine", conflict.local)
        assertEquals("$NOTE_START\nTheirs", conflict.remote)
        assertEquals(listOf("Mine"), state.pending)
        assertFalse(state.canSync)
    }

    @Test
    fun keepTheirsDropsTheQueue() {
        val state = preset(SyncMode.Conflict).then(NotesAction.Resolve(Resolution.KeepTheirs))
        assertNull(state.conflict)
        assertEquals(state.remote, state.local)
        assertTrue(state.pending.isEmpty())
        assertEquals(SyncMode.Online, state.mode)
    }

    @Test
    fun keepMinePushesTheLocalVersionOverTheServer() {
        val conflicted = preset(SyncMode.Conflict)
        val state = conflicted.then(NotesAction.Resolve(Resolution.KeepMine), NotesAction.PushFinished)
        assertEquals(conflicted.conflict!!.local, state.remote)
        assertEquals(SyncMode.Online, state.mode)
    }

    @Test
    fun keepBothMergesWithoutRepeatingSharedLines() {
        val conflicted = preset(SyncMode.Conflict)
        val state = conflicted.then(NotesAction.Resolve(Resolution.KeepBoth), NotesAction.PushFinished)
        assertEquals(
            listOf(NOTE_START, REMOTE_LINES[0], LOCAL_LINES[0], LOCAL_LINES[1]),
            state.remote.lines()
        )
        assertEquals(state.remote, state.local)
        assertEquals(SyncMode.Online, state.mode)
    }

    @Test
    fun resolvingOfflineWaitsForTheNetwork() {
        val state = preset(SyncMode.Conflict).then(NotesAction.GoOffline, NotesAction.Resolve(Resolution.KeepMine))
        assertEquals(SyncMode.Offline, state.mode)
        assertEquals(SyncMode.Syncing, reduce(state, NotesAction.GoOnline).mode)
    }

    @Test
    fun everyPresetShowsItsMode() {
        SyncMode.entries.forEach { mode ->
            assertEquals(mode, preset(mode).mode, "preset for $mode")
            assertEquals(mode, reduce(NotesState(), NotesAction.JumpTo(mode)).mode)
        }
    }

    @Test
    fun nextLineAddsSomethingNewBeforeRepeating() {
        val queued = preset(SyncMode.Queue).local
        assertEquals(LOCAL_LINES[2], nextLine(queued, LOCAL_LINES))
        val all = LOCAL_LINES.fold(NOTE_START, ::appendLine)
        assertTrue(nextLine(all, LOCAL_LINES) in LOCAL_LINES)
        assertEquals(REMOTE_LINES[0], nextLine(NOTE_START, REMOTE_LINES))
    }
}
