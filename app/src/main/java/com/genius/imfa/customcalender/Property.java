package com.genius.imfa.customcalender;

/**
 * This class represents the property object associated with a description.
 * @author PRABIR KUMAR KUNDU
 * @version 1.0
 * @since 2025-01-21
 */
public class Property {

    /**
     * Resource id for the layout to be inflated.
     */
    public int layoutResource = -1;

    /**
     * Resource id for the text view within the date view which will be used to display day of month.
     */
    public int dateTextViewResource = -1;

    /**
     * true if the date view should be enabled, false otherwise.
     */
    public boolean enable = true;

}