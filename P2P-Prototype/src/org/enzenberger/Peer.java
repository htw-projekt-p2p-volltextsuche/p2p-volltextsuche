package org.enzenberger;

public interface Peer {
    /**
     * Store a new String in the P2P-Network.
     * @param string the string to be stored
     * @throws NetworkInactiveException if the network is not active jet
     */
    void safeString(String string) throws NetworkInactiveException;

    /**
     * Search who stored a String
     * @param string the string after which is stored
     * @return the string with the metainformation about who stored it
     * @throws NetworkInactiveException if the network is not active jet
     */
    String searchString(String string) throws NetworkInactiveException;
}
