# FLOAT

## Bugs Document

Aaron So 17150137  
Clarence(Junwei) Su 36387132  
Ming Hin Matthew Lam 33056145  
Rachel Yeo 30032122  
Richard Xie 55786140  
Samuel Farinas 17721144  
Selina Suen 14022140


| Relevant Subsystems | Bug Description | Proposed Solution | 
| --- | --- | --- | --- |
|1 |Facebook API/UI |User can get stuck if they go back from Map to login button if already signed in. They are forced to logout then log back in to redirect to the map page. |Check if user is logged in on resuming the first page and bounce user back to map. Other option is to overlay signin button on map as a pop-up that only disappears when user is signed in.|
|2 |Firebase API| App will crash on Map Page if user launched/saved a campaign with initial coordinates of integer value.| Check if user entered any coordinates without a ‘.’ and add a “.0” to make the string a double. |
