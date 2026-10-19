package com.wabulk.sender

import com.wabulk.sender.update.UpdateManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateManagerTest {

    @Test
    fun testIsNewerVersion() {
        // Newer patch version
        assertTrue(UpdateManager.isNewerVersion("v1.1.2", "1.1.1"))
        assertTrue(UpdateManager.isNewerVersion("1.1.2", "1.1.1"))

        // Newer minor version
        assertTrue(UpdateManager.isNewerVersion("v1.2.0", "1.1.1"))
        assertTrue(UpdateManager.isNewerVersion("1.2.0", "1.1.1"))

        // Newer major version
        assertTrue(UpdateManager.isNewerVersion("v2.0.0", "1.1.1"))

        // Equal version
        assertFalse(UpdateManager.isNewerVersion("v1.1.1", "1.1.1"))
        assertFalse(UpdateManager.isNewerVersion("1.1.1", "1.1.1"))

        // Older version
        assertFalse(UpdateManager.isNewerVersion("v1.1.0", "1.1.1"))
        assertFalse(UpdateManager.isNewerVersion("v1.0.0", "1.1.1"))
        assertFalse(UpdateManager.isNewerVersion("0.9.9", "1.1.1"))

        // Extended semantic versions
        assertTrue(UpdateManager.isNewerVersion("v1.1.1.1", "1.1.1"))
        assertFalse(UpdateManager.isNewerVersion("1.1.1", "1.1.1.1"))
    }
}
