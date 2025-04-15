# Photos32
A multi-user photo application that allows storage and management of photos.

## Project Structure
```
Photos32/
|── bin/
|── data/
|── docs/
│── src/
│   └── photos32/
│       ├── model/
│       │   ├── Album.java
│       │   ├── Photo.java
│       │   ├── Tag.java
│       │   ├── TagType.java
│       │   └── User.java
│       ├── view/
│       │   ├── AdminHome.fxml
│       │   ├── AlbumCard.fxml
│       │   ├── AlbumView.fxml
│       │   ├── FilterWindow.fxml
│       │   ├── Login.fxml
│       │   ├── PhotoCard.fxml
│       │   ├── PhotoView.fxml
│       │   ├── SearchResults.fxml
│       │   └── UserHome.fxml
│       ├── controller/
│       │   ├── AdminHomeController.java
│       │   ├── AlbumCardController.java
│       │   ├── AlbumViewController.java
│       │   ├── FilterController.java
│       │   ├── LoginController.java
│       │   ├── PhotoCardController.java
│       │   ├── PhotoViewController.java
│       │   ├── SearchResultsPopupController.java
│       │   └── UserHomeController.java
│       ├── service/
│       │   ├── AlertUtil.java
│       │   ├── DataStore.java
│       │   └── PhotoService.java
│       └── Photos.java
│── .gitignore
|── README.md
```

## Prerequisites
Before running this project, make sure you have the following installed:
- Java 21
- JavaFX SDK 21.0.6 (You can download the JavaFX SDK from [here](https://gluonhq.com/products/javafx/))

## Getting Started

### Step 1: Clone the Repository

1. 🔐 Generate a GitHub Personal Access Token (PAT)
    - Go to https://github.com/settings/tokens
    - Click "Generate new token" and choose **"Generate new token (**classic**)"** from the dropdown.
    - Under "Select scopes" check the box next to **"repo"**.
    - Click "Generate token" at the bottom.
    - Copy the token and save it somewhere safe!

2. Clone the repository to your local machine using Git:
    ```
    git clone https://github.com/lukewarmCoder/Photos32.git
    ```
    - Git will ask for:
        - **Username:** Your GitHub username
        - **Password:** Paste the **personal access token**

    **Note:** If the PAT method doesn't work, you can always just download the Zip file manually. Just be sure to rename the extracted folder from "Photos32-main" to "Photos32".
   
4. Navigate to the project folder:
   
    ```
    cd Photos32
    ```

### Step 2: Set the path to your JavaFX SDK

1. Identify the path to the lib/ folder inside the JavaFX SDK.
    - Example: /Downloads/javafx-sdk-21.0.6/lib/

2. Set the temporary variable PATH_TO_FX to your JavaFX SDK path:
    - On Mac/Linux:
      
      ```
      export PATH_TO_FX=/path/to/javafx-sdk/lib/
      ```
      
    - On Windows (Command Prompt):
      
        ```
        set PATH_TO_FX=C:\path\to\javafx-sdk\lib\
        ```

     (This will only affect the current terminal session and will not persist after you close the terminal.)

### Step 3: Run the Application

1. Run the following command.
    - On Mac/Linux:
        
        ```
        java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp bin photos32.Photos
        ```
      
    - On Windows (Command Prompt):
      
        ```
        java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp bin photos32.Photos
        ```

### Step 4: Enjoy the App!

Once you've completed the above steps, the application should start and the interface should be displayed. 
If you have any issues running the application please reach out to us.




