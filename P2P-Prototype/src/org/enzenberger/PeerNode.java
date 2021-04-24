package org.enzenberger;

public interface PeerNode {
    /**
     * Call to register a new Peer. Is only possible before the network is active because the networks needs to know
     * all members to distribute its storage/search capabilities evenly
     * @param peerClient the new PeerNode to register
     * @throws NetworkActiveException if the network is already active
     */
    void registerPeer(PeerClient peerClient) throws NetworkActiveException;

    /**
     * is called by the node that initially registered a new Peer to notify the other nodes in the network about its
     * existence
     * @param peerClient the new PeerNode
     * @throws NetworkActiveException if the network is already active
     */
    void notifyNewPeer(PeerClient peerClient) throws NetworkActiveException;

    /**
     * Invoke before utilizing networking capability. Is irreversible since network neither grows nor shrinks
     * @throws NetworkActiveException if the network is already active
     */
    void startNetwork() throws NetworkActiveException;

    /**
     * Gets called by the node which got the initial "startNetwork" call
     * @throws NetworkActiveException if the network is already active
     */
    void setNetworkAktive() throws NetworkActiveException;

    /**
     * Search for a result on a peerNode
     * @param string the string to be searched for
     * @return the string with metainformation about its belongings
     * @throws NetworkInactiveException if the Network is not active jet
     */
    String getSearchResult(String string) throws NetworkInactiveException;

    /**
     * Store a string on a peerNode
     * @param string the string to be stored
     * @throws NetworkInactiveException if the network is not active jet
     */
    void storeString(String string) throws NetworkInactiveException;
}
