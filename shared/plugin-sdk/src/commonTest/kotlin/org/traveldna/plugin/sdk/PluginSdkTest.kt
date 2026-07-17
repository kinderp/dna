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
        assertFailsWith<IllegalArgumentException> { PluginId("org.traveldna.fake-") }
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

    @Test
    fun descriptorSnapshotsMutableCollections() {
        val capabilities = mutableSetOf(CapabilityId("routing.plan"))
        val platforms = mutableSetOf(KnownPlatforms.Jvm)
        val notices = mutableListOf(LicenseNotice("fixture", "MIT"))
        val descriptor = PluginDescriptor(
            id = PluginId("org.traveldna.snapshot-test"),
            implementationVersion = "0.1.0",
            contractVersion = 1,
            capabilities = capabilities,
            supportedPlatforms = platforms,
            licenseNotices = notices,
        )

        capabilities.clear()
        platforms.clear()
        notices.clear()

        assertEquals(setOf(CapabilityId("routing.plan")), descriptor.capabilities)
        assertEquals(setOf(KnownPlatforms.Jvm), descriptor.supportedPlatforms)
        assertEquals(listOf(LicenseNotice("fixture", "MIT")), descriptor.licenseNotices)
    }
}
