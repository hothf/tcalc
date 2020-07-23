package de.ka.jamit.tcalc.repo.db

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import de.ka.jamit.tcalc.R

/**
 * Contains all database models.
 *
 * Created by Thomas Hofmann on 23.07.20
 **/
@Keep
@Entity(tableName = "users")
data class User(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                var name: String = "",
                var selected: Boolean = false)

@Keep
@Entity(tableName = "records",
        foreignKeys = [ForeignKey(entity = User::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("user_id"),
                onDelete = CASCADE)])
data class Record(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                  var key: String = "",
                  var value: Float = 0.0f,
                  var timeSpan: TimeSpan = TimeSpan.MONTHLY,
                  var category: Category = Category.COMMON,
                  var isConsidered: Boolean = true,
                  var isIncome: Boolean = false,
                  @ColumnInfo(name = "user_id") var userId: Int)

class Converters {
    @TypeConverter
    fun timeSpanToDatabase(entityProperty: TimeSpan?): Int {
        return entityProperty?.id ?: 0
    }

    @TypeConverter
    fun timeSpanFromDatabase(databaseValue: Int?): TimeSpan {
        val entry = TimeSpan.values().find { it.id == databaseValue }
        return entry ?: TimeSpan.MONTHLY
    }

    @TypeConverter
    fun categoryToDatabase(entityProperty: Category?): Int {
        return entityProperty?.id ?: 0
    }

    @TypeConverter
    fun categoryFromDatabase(databaseValue: Int?): Category {
        val entry = Category.values().find { it.id == databaseValue }
        return entry ?: Category.COMMON
    }
}

enum class Category(val id: Int, @DrawableRes val resId: Int, @ColorRes val shadeRes: Int) {
    COMMON(0, R.drawable.ic_saving_common, R.color.shade_one),
    HOUSE(1, R.drawable.ic_saving_home, R.color.shade_two),
    SAVING(2, R.drawable.ic_saving_bank, R.color.shade_three),
    CAR(3, R.drawable.ic_saving_car, R.color.shade_four),
    INCOME(4, R.drawable.ic_saving_income, R.color.shade_five),
    INSURANCE(5, R.drawable.ic_saving_insurance, R.color.shade_six),
    FUN(6, R.drawable.ic_saving_fun, R.color.shade_seven),
    EAT(7, R.drawable.ic_saving_eat, R.color.shade_eight)
}

enum class TimeSpan(val id: Int, @StringRes val translationRes: Int) {
    MONTHLY(0, R.string.enum_timespan_monthly),
    QUARTERLY(1, R.string.enum_timespan_quarterly),
    YEARLY(2, R.string.enum_timespan_yearly)
}