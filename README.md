# CP470-questionnaireApp

This app was made for WLU CP470 - Android Programming.

Features:
- Uses Fragments
    - Uses recyclerview for easy scrolling
    - Each recyclerview item is a fragment view
- SQLite for data storage
    - Store string data to/from model objects

The first app is a questionnaire-like app that uses a recyclerview to display a list of objects. 
The object we are displaying shows a thumbnail sized picture and the question text on the recyclerview list. 
If we tap on a list object, the fragment would then load a new view containing a larger image of the thumbnail 
and the answer to the question below the image. The user can also create a new list object, however currently 
the app only allows images already included with the app (i.e. we cannot use our own photos from the Gallery).
We can also delete contents of the list by swiping left. Lastly, this app utilizes SQLite to save the contents
of the recyclerview. This means that if the user were to add/delete entries of the recyclerview, that action 
would be saved when the app is put to sleep, or when the app closes completely. 
