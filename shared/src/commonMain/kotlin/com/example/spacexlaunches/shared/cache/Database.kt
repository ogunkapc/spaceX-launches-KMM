package com.example.spacexlaunches.shared.cache

import com.example.spacexlaunches.cache.AppDatabase
import com.example.spacexlaunches.shared.entity.Links
import com.example.spacexlaunches.shared.entity.Patch
import com.example.spacexlaunches.shared.entity.RocketLaunch

internal class Database (databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

//    function to clear all the tables in the database in a single SQL transaction
    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllLaunches()
        }
    }

//    function to get a list of all the rocket launches
    internal fun getAllLaunches(): List<RocketLaunch> {
        return dbQuery.selectAllLaunchesInfo(::mapLaunchSelecting).executeAsList()
    }

    private fun mapLaunchSelecting(
        flightNumber: Long,
        missionName: String,
        details: String?,
        launchSuccess: Boolean?,
        launchDateUTC: String,
        patchUrlSmall: String?,
        patchUrlLarge: String?,
        articleUrl: String?
    ) : RocketLaunch{
        return RocketLaunch(
            flightNumber = flightNumber.toInt(),
            missionName = missionName,
            details = details,
            launchDateUTC = launchDateUTC,
            launchSuccess = launchSuccess,
            links = Links(
                patch = Patch(
                    small = patchUrlSmall,
                    large = patchUrlLarge
                ),
                article = articleUrl
            )
        )
    }

//    function to insert data into the database
    internal fun createLaunches(launches: List<RocketLaunch>) {
        dbQuery.transaction {
            launches.forEach { launch ->
                insertLaunch(launch)
            }
        }
    }

    private fun insertLaunch(launch: RocketLaunch) {
        dbQuery.insertLaunch(
            flightNumber = launch.flightNumber.toLong(),
            missionName = launch.missionName,
            details = launch.details,
            launchSuccess = launch.launchSuccess ?: false,
            launchDateUTC = launch.launchDateUTC,
            patchUrlSmall = launch.links.patch?.small,
            patchUrlLarge = launch.links.patch?.large,
            articleUrl = launch.links.article
        )
    }
}