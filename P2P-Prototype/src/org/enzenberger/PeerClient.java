package org.enzenberger;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PeerClient implements Peer, PeerNode {
    //Key-Value pair of the Numbers of the clients and the clients themselves. In reality there would be ips saved
    //instead of the actual client.
    private Hashtable<Integer, PeerClient> peers;

    // the stored String designated to this peer only
    private final List<String> storage = new LinkedList<>();

    //the id of the peer
    public final String id;

    //the number assigned by the network
    private Integer number;

    //the status of the network. inactive = peers can be added. active = search and storage can be utilized
    Boolean networkAktiv = false;

    PeerClient(String id) {
        this.id = id;
    }

    @Override
    public void safeString(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        //get hash-value of string to find on which peer to store the string
        int storageNumber = getHash(string);
        peers.get(storageNumber).storeString(string);
    }

    @Override
    public void storeString(String string) {
        this.storage.add(string);
    }

    @Override
    public String searchString(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        int storageNumber = getHash(string);
        return peers.get(storageNumber).getSearchResult(string);
    }

    private int getHash(String string) {
        char firstChar = string.replace(" ", "").toLowerCase().charAt(0);
        return (int) firstChar % peers.size();
    }

    @Override
    public String getSearchResult(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        for (String storedString : storage) {
            if (string.equals(storedString)) return number +" - "+ id + ": I know the way to " + storedString;
        }
        return number + " - " + id + ": There is none String like this";
    }

    @Override
    public void registerPeer(PeerClient peerClient) throws NetworkActiveException {
        if (networkAktiv) throw new NetworkActiveException();
        //the first peer needs to create a hashtable storing itself first.
        if (this.peers == null) {
            this.peers = new Hashtable<>();
            int number = 0;
            this.peers.put(number, this);
            this.setNumber(number);
        }
        //only if the new peer is not registered jet should it be saved
        if (!this.peers.contains(peerClient)) {
            int newPeerNumber = peers.size();
            this.peers.put(newPeerNumber, peerClient);
            peerClient.setNumber(newPeerNumber);
            Set<Integer> peerClients = peers.keySet();
            //all already registered peers need to be notified about the new peer. The same goes in the other direction
            for (Integer key : peerClients) {
                PeerClient addressedClient = peers.get(key);
                addressedClient.notifyNewPeer(peerClient);
                peerClient.notifyNewPeer(addressedClient);
            }
        }
    }

    @Override
    public void notifyNewPeer(PeerClient peerClient) throws NetworkActiveException {
        if (networkAktiv) throw new NetworkActiveException();
        if (this.peers == null) this.peers = new Hashtable<>();
        if (!this.peers.contains(peerClient)) {
            this.peers.put(peerClient.getNumber(), peerClient);
        }
    }

    private void setNumber(int newPeerNumber) {
        if (this.number == null) this.number = newPeerNumber;
        else throw new IllegalArgumentException("Number can only be set once!");
    }

    public int getNumber() {
        return this.number;
    }

    @Override
    public void startNetwork() throws NetworkActiveException {
        if (networkAktiv) throw new NetworkActiveException();
        Set<Integer> peerClients = peers.keySet();
        for (Integer key : peerClients) {
            peers.get(key).setNetworkAktive();
        }
    }

    @Override
    public void setNetworkAktive() throws NetworkActiveException {
        if (networkAktiv) throw new NetworkActiveException();
        networkAktiv = true;
        System.out.println(number + " - " + id + ": I am alive");
    }
}
