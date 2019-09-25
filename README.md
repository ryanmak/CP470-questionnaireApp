# CP470-CBCApp
This app was made for WLU CP470 - Android Programming.

Features:
- Internet connection to CBC.ca
- XML data parser
- Recyclerview 
In this app, we attempt to connect to a CBC server that will give us an XML file. This XML file will contain information on the current top stories of the hour. Using HTTP, we connect to the CBC server then save the XML to local storage. This file is then read and processed into a recyclerview list. Each recyclerview item contains the thumbnail of the article and the title of the article. If the user taps on a list item, a larger image and the synopsis of the article is displayed, along with a button that, if clicked, will open a webview page that shows the entire article. 
