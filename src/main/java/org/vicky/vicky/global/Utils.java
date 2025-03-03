package org.vicky.vicky.global;

import jakarta.persistence.EntityManager;
import org.vicky.utilities.ConfigManager;
import org.vicky.utilities.DatabaseManager.HibernateDatabaseManager;
import org.vicky.utilities.DatabaseManager.SQLManager;
import org.vicky.utilities.Theme.ThemeStorer;

public class Utils {
    public static ConfigManager manager;
    public static HibernateDatabaseManager databaseManager;
    public static SQLManager sqlManager;
    public static EntityManager em;
    public static ThemeStorer storer;
}
