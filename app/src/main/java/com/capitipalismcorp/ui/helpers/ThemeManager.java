package com.capitipalismcorp.ui.helpers;

import android.app.Activity;

import com.capitipalismcorp.R;

public class ThemeManager {
    public static void setApplicationTheme(Activity a, String group_theme) {
        if (group_theme.equals("Default")) {
            a.setTheme(R.style.Default);
        } else if (group_theme.equals("Amber")) {
            a.setTheme(R.style.Amber);
        } else if (group_theme.equals("Pink")) {
            a.setTheme(R.style.Pink);
        } else if (group_theme.equals("BrickRed")) {
            a.setTheme(R.style.BrickRed);
        } else if (group_theme.equals("Coffee")) {
            a.setTheme(R.style.Coffee);
        } else if (group_theme.equals("Jade")) {
            a.setTheme(R.style.Jade);
        } else if (group_theme.equals("Cyan")) {
            a.setTheme(R.style.Cyan);
        } else if (group_theme.equals("Mustard")) {
            a.setTheme(R.style.Mustard);
        } else if (group_theme.equals("Navy")) {
            a.setTheme(R.style.Navy);
        } else if (group_theme.equals("RedOrange")) {
            a.setTheme(R.style.RedOrange);
        } else if (group_theme.equals("Turquoise")) {
            a.setTheme(R.style.Turquoise);
        } else if (group_theme.equals("Ochre")) {
            a.setTheme(R.style.Ochre);
        } else if (group_theme.equals("Burnt")) {
            a.setTheme(R.style.Burnt);
        } else if (group_theme.equals("Sapphire")) {
            a.setTheme(R.style.Sapphire);
        } else if (group_theme.equals("Marron")) {
            a.setTheme(R.style.Marron);
        } else if (group_theme.equals("Teal")) {
            a.setTheme(R.style.Teal);
        } else if (group_theme.equals("BlackBrown")) {
            a.setTheme(R.style.BlackBrown);
        } else if (group_theme.equals("Sepia")) {
            a.setTheme(R.style.Sepia);
        } else if (group_theme.equals("Forest")) {
            a.setTheme(R.style.Forest);
        } else if (group_theme.equals("Violet")) {
            a.setTheme(R.style.Violet);
        } else if (group_theme.equals("Scarlet")) {
            a.setTheme(R.style.Scarlet);
        } else if (group_theme.equals("Olive")) {
            a.setTheme(R.style.Olive);
        } else {
            a.setTheme(R.style.Default);
        }
    }

    public static void setApplicationThemeActionBar(Activity a, String group_theme) {
        if (group_theme.equals("Default")) {
            a.setTheme(R.style.DefaultActionBar);
        } else if (group_theme.equals("Amber")) {
            a.setTheme(R.style.AmberActionBar);
        } else if (group_theme.equals("Pink")) {
            a.setTheme(R.style.PinkActionBar);
        } else if (group_theme.equals("BrickRed")) {
            a.setTheme(R.style.BrickRedActionBar);
        } else if (group_theme.equals("Coffee")) {
            a.setTheme(R.style.CoffeeActionBar);
        } else if (group_theme.equals("Jade")) {
            a.setTheme(R.style.JadeActionBar);
        } else if (group_theme.equals("Cyan")) {
            a.setTheme(R.style.CyanActionBar);
        } else if (group_theme.equals("Mustard")) {
            a.setTheme(R.style.MustardActionBar);
        } else if (group_theme.equals("Navy")) {
            a.setTheme(R.style.NavyActionBar);
        } else if (group_theme.equals("RedOrange")) {
            a.setTheme(R.style.RedOrangeActionBar);
        } else if (group_theme.equals("Turquoise")) {
            a.setTheme(R.style.TurquoiseActionBar);
        } else if (group_theme.equals("Ochre")) {
            a.setTheme(R.style.OchreActionBar);
        } else if (group_theme.equals("Burnt")) {
            a.setTheme(R.style.BurntActionBar);
        } else if (group_theme.equals("Sapphire")) {
            a.setTheme(R.style.SapphireActionBar);
        } else if (group_theme.equals("Marron")) {
            a.setTheme(R.style.MarronActionBar);
        } else if (group_theme.equals("Teal")) {
            a.setTheme(R.style.TealActionBar);
        } else if (group_theme.equals("BlackBrown")) {
            a.setTheme(R.style.BlackBrownActionBar);
        } else if (group_theme.equals("Sepia")) {
            a.setTheme(R.style.SepiaActionBar);
        } else if (group_theme.equals("Forest")) {
            a.setTheme(R.style.ForestActionBar);
        } else if (group_theme.equals("Violet")) {
            a.setTheme(R.style.VioletActionBar);
        } else if (group_theme.equals("Scarlet")) {
            a.setTheme(R.style.ScarletActionBar);
        } else if (group_theme.equals("Olive")) {
            a.setTheme(R.style.OliveActionBar);
        } else {
            a.setTheme(R.style.DefaultActionBar);
        }
    }
}
