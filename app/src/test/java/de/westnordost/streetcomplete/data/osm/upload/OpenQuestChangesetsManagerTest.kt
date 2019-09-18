package de.westnordost.streetcomplete.data.osm.upload

import android.content.SharedPreferences
import de.westnordost.osmapi.map.MapDataDao
import de.westnordost.streetcomplete.any
import de.westnordost.streetcomplete.data.changesets.OpenChangeset
import de.westnordost.streetcomplete.data.changesets.OpenChangesetsDao
import de.westnordost.streetcomplete.data.osm.OsmElementQuestType
import de.westnordost.streetcomplete.on
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class OpenQuestChangesetsManagerTest {

    private lateinit var questType: OsmElementQuestType<*>
    private lateinit var osmDao: MapDataDao
    private lateinit var openChangesetsDB: OpenChangesetsDao
    private lateinit var changesetAutoCloser: ChangesetAutoCloser
    private lateinit var manager: OpenQuestChangesetsManager
    private lateinit var prefs: SharedPreferences

    @Before fun setUp() {
        questType = mock(OsmElementQuestType::class.java)
        osmDao = mock(MapDataDao::class.java)
        openChangesetsDB = mock(OpenChangesetsDao::class.java)
        changesetAutoCloser = mock(ChangesetAutoCloser::class.java)
        prefs = mock(SharedPreferences::class.java)
        manager = OpenQuestChangesetsManager(osmDao, openChangesetsDB, changesetAutoCloser, prefs)
    }

    @Test fun `create new changeset if none exists`() {
        on(openChangesetsDB.get(any(), any())).thenReturn(null)
        on(osmDao.openChangeset(any())).thenReturn(123L)

        assertEquals(123L, manager.getOrCreateChangeset(questType, "my source"))

        verify(osmDao).openChangeset(any())
        verify(openChangesetsDB).put(any())
    }

    @Test fun `reuse changeset if one exists`() {
        on(openChangesetsDB.get(any(), any())).thenReturn(OpenChangeset("bla", "source", 123))

        assertEquals(123L, manager.getOrCreateChangeset(questType, "my source"))

        verify(osmDao, never()).openChangeset(any())
    }
}
