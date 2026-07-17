package org.traveldna.plugin.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PluginSdkTest {
    @Test
    fun validatesNamespacedIdentifiers() {
        assertEquals("routing.plan", CapabilityId("routing.plan").value)
        assertFailsWith<IllegalArgumentException> { CapabilityId("Routing Plan") }
        assertFailsWith<IllegalArgumentException> { PluginId("fake") }
    }

    @Test
    fun descriptorRequiresCapabilitiesAndPlatforms() {
        assertFailsWith<IllegalArgumentException> {
            PluginDescriptor(
                id = PluginId("org.traveldna.empty"),
                implementationVersion = "0.1.0",
                contractVersion = 1,
                capabilities = emptySet(),
                supportedPlatforms = setOf(KnownPlatforms.Jvm),
            )
        }
    }
}
