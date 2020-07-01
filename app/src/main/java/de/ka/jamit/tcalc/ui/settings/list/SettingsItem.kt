package de.ka.jamit.tcalc.ui.settings.list

class SettingsItem(val title: String, val id: Int) {

    override fun equals(other: Any?): Boolean {
        if (other is SettingsItem) {
            return other.title == title && other.id == id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + id
        return result
    }
}