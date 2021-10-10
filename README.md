# CS-4518-Mobile-and-Ubiquitous-Computing
# Note-Taking-App

It is an Android based application that will allow its users to create their account, take notes on the go, and add location and pictorial information to it. The App makes uses of the Model-View-Controller (MVC) architecture. The data will be stored in two different areas for peristence: Local Sqlite3 DB and MongoDB (For longer persistence). It also allows the users to utilize the “list-detail” methodology to capture the notes and label them under different titles. This allows for better organization of information as well as easier retrievability. 
 
## Features

### Login and User Profile Management

The Note-Taking-App allows the user to create profile as well as use their existing profile to keep their notes at one place, attached to their username.

![1](https://user-images.githubusercontent.com/47456525/136677780-eb739320-8dab-495d-966b-da8e62518a11.png)

### Notes Organization

The Note-Taking-App allows the user to add a new note, view and edit their organized notes, save the current list of notes to the MongoDB server as well as load the list of notes associated with their username from the MongoDB server.

![2](https://user-images.githubusercontent.com/47456525/136677829-8e9dffe3-5771-4045-a640-8ba11887e72e.png)

### Adding Image and Location information to the notes

Apart from being a traditional note-taker, the Note-Taking-App allows the user to add location as well as pictorial information to their notes.

![3](https://user-images.githubusercontent.com/47456525/136677885-fc9b8bf2-9034-457b-a6fd-c729412030ef.png)

## Testing for the Note-Taking-App

For testing the correctness and expected behaviour of our app, we performed UI tests using the Espresso Library in Android. 

![image](https://user-images.githubusercontent.com/32044950/136706532-1e6b1d3c-7a39-4579-aeeb-2133746dda24.png)


## Resource Utilization

### App Startup

This is the resource profile of the startup sequence of our app. It can be seen that even before the main activity starts our app causes a light burst of energy usage and around 10% of CPU power. The second burst after our app’s activity starts is caused by the main activity’s lifecycle functions such as onCreate(). This burst lasts for a shorter amount of time than the first but uses more energy and CPU power at its peak.

![7](https://user-images.githubusercontent.com/47456525/136678047-2010fe21-e284-4650-85d3-7bffcf514df3.png)

### User Login

Here, we can see that there is a spike in network usage as our app makes a POST API call to our web server to authenticate a user. The app then stores the generated authentication token in the local file system in the mobile, so that it can be used next time for automatic login in the same device.There is a significant use of CPU as well but no significant increase in the energy utilization

![4](https://user-images.githubusercontent.com/47456525/136677938-0aeefe60-2852-4160-b324-8397e7d1805a.png)

### Loading Data from Server

As can be seen from the burst of network and CPU activity, that is where the app is trying to request note data from the server. Here, the device is mainly receiving data and then storing the result in its local database. It can be seen that a very small blip in the device’s memory is made where the device stores the data from the network request before putting it into the SQL database. The energy usage for this action is light at most according to the profiler.

![5](https://user-images.githubusercontent.com/47456525/136677967-dd1cd365-8a2c-4bd6-9ce5-39ec96a9befb.png)

### Saving data from local Sqlite3 database to MongoDB Server 
(Includes adding a new note, and using location services to fetch the current location related to the mobiles GPS position)

This operation makes use of the “Save to Server” button in the Note List Fragment. It makes a POST API call to our web server with the json body containing the list of tokens and their associated username and password. This shows an increase in network and CPU utilization because of the size of the POST API call’s body, as it contains a lot of data which is serialized using the GSON library.

![6](https://user-images.githubusercontent.com/47456525/136678019-788fb28d-5d67-491a-8dd3-b6b9376e917d.png)

### Switching to the note detail fragment

This action results in a CPU and energy utilization spike for a small amount of time as our app requests the local sqlite database for the note lookup and populates the detail fragment with the necessary data.

![8](https://user-images.githubusercontent.com/47456525/136678068-aaf8562a-b078-4a9e-8278-badd73cc95ce.png)

### Editing the note title and body

The usage of CPU and energy in the above screen shot is due to typing in the EditTexts within our note detail fragment. There is a small amount of energy and CPU usage each time some text changes within the EditTexts due to a callback we registered that runs to update the fragment’s ViewModel everytime the text in the EditText changes. The inconsistencies in the energy and CPU usage can be attributed to the varying typing speeds of different words.

![9](https://user-images.githubusercontent.com/47456525/136678084-725935b7-e929-495d-8910-e9548f6a3893.png)

### Setting a note’s location

Using the “add location” button to fetch the device’s current GPS location isn’t too much work. It takes a very light amount of energy consumption and a sudden, short increase in CPU utilization.

![10](https://user-images.githubusercontent.com/47456525/136678097-0cb1aff6-e50e-4e43-b47c-f9a588574de8.png)

### Taking a picture for a note and rotating the device

This action involves opening up the camera application using implicit intents, rotating the device in the landscape orientation, capturing the picture, generating its bitmap, and saving it in the file system on the device. This shows increases in CPU and energy utilization on major events (shooting up the camera application, device rotation, bitmap generation etc).

![11](https://user-images.githubusercontent.com/47456525/136678110-4474f4e9-90b6-4e01-8801-9a77484e5115.png)

### Saving note data and switching to the note list fragment

In this screenshot two actions are happening almost exactly at the same time. We are exiting the note detail fragment to go back to the note list fragment and saving the note’s data to the local database. Having these two actions happen at the same time causes a very short but sharp spike in CPU and energy usage where the CPU nearly reaches 50% and the energy usage is medium.

![12](https://user-images.githubusercontent.com/47456525/136678128-cf766749-a358-44f7-bc11-fe41f4ad4425.png)

#### Note about memory utilization
The memory utilization is almost constant throughout the whole application usage (around 100MB). It is very minutely affected by the features in our app.





