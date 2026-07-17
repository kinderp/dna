package org.traveldna.plugin.sdk

import kotlin.jvm.JvmInline

private val NAMESPACED_ID = Regex("[a-z][a-z0-9]*(?:\\.[a-z][a-z0-9]*(?:-[a-z0-9]+)*)+")

private fun requireNamespacedId(value: String, field: String) {
    require(value.length in 3..128) { "$field length must be within [3, 128]" }
    require(NAMESPACED_ID.matches(value)) {
        "$field must be a lowercase dot-separated identifier"
    }
}

@JvmInline
value class PluginId(val value: String) {
    init { requireNamespacedId(value, "plugin id") }
    override fun toString(): String = value
}

@JvmInline
value class CapabilityId(val value: String) {
    init { requireNamespacedId(value, "capability id") }
    override fun toString(): String = value
}

@JvmInline
value class PlatformId(val value: String) {
    init { requireNamespacedId(value, "platform id") }
    override fun toString(): String = value
}

data class LicenseNotice(
    val component: String,
    val spdxExpression: String,
    val notice: String? = null,
) {
    init {
        require(component.isNotBlank()) { "license component must not be blank" }
        require(spdxExpression.isNotBlank()) { "SPDX expression must not be blank" }
    }
}

/** Immutable snapshot of the metadata used to select and diagnose one plugin. */
class PluginDescriptor(
    val id: PluginId,
    val implementationVersion: String,
    val contractVersion: Int,
    capabilities: Set<CapabilityId>,
    supportedPlatforms: Set<PlatformId>,
    licenseNotices: List<LicenseNotice> = emptyList(),
) {
    val capabilities: Set<CapabilityId> = capabilities.toSet()
    val supportedPlatforms: Set<PlatformId> = supportedPlatforms.toSet()
    val licenseNotices: List<LicenseNotice> = licenseNotices.toList()

    init {
        require(implementationVersion.isNotBlank()) { "implementation version must not be blank" }
        require(implementationVersion.length <= 64) { "implementation version is too long" }
        require(contractVersion > 0) { "contract version must be positive" }
        require(this.capabilities.isNotEmpty()) { "a plugin must declare at least one capability" }
        require(this.supportedPlatforms.isNotEmpty()) { "a plugin must declare at least one platform" }
    }

    override fun equals(other: Any?): Boolean =
        other is PluginDescriptor &&
            id == other.id &&
            implementationVersion == other.implementationVersion &&
            contractVersion == other.contractVersion &&
            capabilities == other.capabilities &&
            supportedPlatforms == other.supportedPlatforms &&
            licenseNotices == other.licenseNotices

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + implementationVersion.hashCode()
        result = 31 * result + contractVersion
        result = 31 * result + capabilities.hashCode()
        result = 31 * result + supportedPlatforms.hashCode()
        result = 31 * result + licenseNotices.hashCode()
        return result
    }

    override fun toString(): String =
        "PluginDescriptor(id=$id, implementationVersion=$implementationVersion, " +
            "contractVersion=$contractVersion, capabilities=$capabilities, " +
            "supportedPlatforms=$supportedPlatforms, licenseNotices=$licenseNotices)"
}

interface TravelDnaPlugin {
    val descriptor: PluginDescriptor
}

object KnownPlatforms {
    val KotlinCommon = PlatformId("kotlin.common")
    val Jvm = PlatformId("kotlin.jvm")
    val Android = PlatformId("mobile.android")
    val Ios = PlatformId("mobile.ios")
    val LinuxX64 = PlatformId("native.linux-x64")
}
