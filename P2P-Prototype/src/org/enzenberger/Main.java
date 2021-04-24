package org.enzenberger;

public class Main {

    public static void main(String[] args) {
        //create Peers with unique ids
        PeerClient peerOne = new PeerClient("peerOne");
        PeerClient peerTwo = new PeerClient("peerTwo");
        PeerClient peerThree = new PeerClient("peerThree");
        PeerClient peerFour = new PeerClient("peerFour");
        PeerClient peerFive = new PeerClient("peerFive");
        PeerClient peerSix = new PeerClient("peerSix");
        PeerClient peerSeven = new PeerClient("peerSeven");
        PeerClient peerEight = new PeerClient("peerEight");

        //register the different peers with other peers that are already part of the network
        try {
            peerOne.registerPeer(peerTwo);
            peerTwo.registerPeer(peerThree);
            peerTwo.registerPeer(peerFour);
            peerFour.registerPeer(peerFive);
            peerOne.registerPeer(peerSix);
            peerSix.registerPeer(peerSeven);
            peerOne.registerPeer(peerEight);

            //start networking
            peerFour.startNetwork();
        } catch (NetworkActiveException e) {
            e.printStackTrace();
        }

        System.out.println();


        try {
            //save some random strings in the network specifying an entrypoint. The String are not
            //necessarily saved at that location.
            peerOne.safeString("Haus");
            peerTwo.safeString("Apfel");
            peerFour.safeString("Besenstiel");
            peerSeven.safeString("Rapunsel");
            peerSix.safeString("Mama");
            peerSix.safeString("Nonne");
            peerEight.safeString("Waffel");

            //search for Strings and print the resulting String containing metadata and the result.
            System.out.println(peerEight.searchString("Besenstiel"));
            System.out.println(peerSix.searchString("Mama"));
            System.out.println(peerOne.searchString("Apfel"));
            System.out.println(peerSix.searchString("Nonne"));
        } catch (NetworkInactiveException e) {
            e.printStackTrace();
        }

    }
}
