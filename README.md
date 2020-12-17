# Delhi Transit Android Client ![Android CI](https://github.com/delhitransit/delhitransit-android/workflows/Android%20CI/badge.svg)

The Android Client for our project.

## Introduction
Delhi Transit is a server and client app that displays information like distance, routes, stops,
timings etc related to DTC Buses in a user friendly UI based on Android which is backboned by
our servers based on Java SpringBoot and database management by PostgreSQL.

Our server manages the real large and heavy raw data coming right from the Delhi Government
containing information about buses.The server performs various tasks like parsing raw data to
Java objects which can further be used to create a database, finding ways to travel within different
bus stops, finding the most optimal route, etc. After these tasks the last thing a server can do is
creating APIs.

## Features
- When user launches the app, the app will check for location services and requests user location to get the bus stops near his/her location.
- When user launches the app, it also checks for the server whether it is online or offline and it notifies that the server is offline.
- On launch the app also checks for the Wi-Fi or data services are turned on so that the app is ready to request data from server.
- After launch the app displays a map and search box to search the routes available between a source and destination stop.
- After the search the results are displayed in a bottom sheet in which the user can further see the route plotting on the map for each bus.
- User can mark some stops as favourites or most visited so that he/she doesn't need to type in the search.
- On clicking a particular stop on the map displays the list of buses that will pass through that stop in upcoming time.
