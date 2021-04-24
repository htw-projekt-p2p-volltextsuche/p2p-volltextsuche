package org.enzenberger;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PeerClient implements Peer, PeerNode{
    private Hashtable<Integer, PeerClient> peers;
    private final List<String> storage = new LinkedList<>();
    public final String id;
    private Integer number;
    Boolean networkAktiv = false;

    PeerClient(String id){
        this.id = id;
    }

    @Override
    public void safeString(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        char firstChar = string.replace(" ", "").toLowerCase().charAt(0);
        int storageNumber = (int) firstChar % peers.size();
        peers.get(storageNumber).storeString(string);
    }

    @Override
    public void storeString(String string) {
        this.storage.add(string);
    }

    @Override
    public String searchString(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        char firstChar = string.replace(" ", "").toLowerCase().charAt(0);
        int storageNumber = (int) firstChar % peers.size();
        return peers.get(storageNumber).getSearchResult(string);
    }

    @Override
    public String getSearchResult(String string) throws NetworkInactiveException {
        if (!networkAktiv) throw new NetworkInactiveException();
        for(String storedString: storage){
            if (string.equals(storedString)) return number + id + ": Found String " + storedString;
        }
        return number + id + ": There is none String like this";
    }

    @Override
    public void registerPeer(PeerClient peerClient) throws NetworkActiveException {
        if(networkAktiv)throw new NetworkActiveException();
        if(this.peers == null) {
            this.peers = new Hashtable<>();
            int number = 0;
            this.peers.put(number, this);
            this.setNumber(number);
        }
        if(!this.peers.contains(peerClient)) {
            int newPeerNumber= peers.size();
            this.peers.put(newPeerNumber, peerClient);
            peerClient.setNumber(newPeerNumber);
            Set<Integer> peerClients = peers.keySet();
            for(Integer key: peerClients){
                PeerClient addressedClient = peers.get(key);
                addressedClient.notifyNewPeer(peerClient);
                peerClient.notifyNewPeer(addressedClient);
            }
        }
    }

    @Override
    public void notifyNewPeer(PeerClient peerClient) throws NetworkActiveException {
        if(networkAktiv)throw new NetworkActiveException();
        if(this.peers == null) this.peers = new Hashtable<>();
        if(!this.peers.contains(peerClient)){
            this.peers.put(peerClient.getNumber(), peerClient);
        }
    }

    private void setNumber(int newPeerNumber) {
        if (this.number == null) this.number = newPeerNumber;
        else throw new IllegalArgumentException("Number can only be set once!");
    }

    public int getNumber(){
        return this.number;
    }

    @Override
    public void startNetwork() throws NetworkActiveException {
        if(networkAktiv)throw new NetworkActiveException();
        Set<Integer> peerClients = peers.keySet();
        for (Integer key: peerClients){
            peers.get(key).setNetworkAktive();
        }
    }

    @Override
    public void setNetworkAktive() throws NetworkActiveException {
        if (networkAktiv)throw new NetworkActiveException();
        networkAktiv = true;
        System.out.println(number + id +": I am alive");
    }
}
