package photos32.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import photos32.model.User;

/**
 * Service class for storing user data.
 */
public class DataStore {
    private static final String DATA_DIR = "data/";

    /**
     * Saves the current user data to a file.
     * The user's data is serialized and stored in a file named after their username.
     * 
     * @param user the user to be saved
     */
    public static void saveUser(User user) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a user object from disk if it exists.
     *
     * @param username the username of the user to load
     * @return the loaded {@link User} object, or null if not found
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static User loadUser(String username) throws IOException, ClassNotFoundException {
        File userFile = new File("data/" + username + ".dat");
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (User)ois.readObject();
            }
        }
        return null; // User doesn't exist 
    }
}
