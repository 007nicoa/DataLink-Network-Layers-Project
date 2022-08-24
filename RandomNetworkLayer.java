// =============================================================================
// IMPORTS

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
// =============================================================================



// =============================================================================
/**
 * @file   RandomNetworkLayer.java
 * @author Scott F. Kaplan (sfkaplan@cs.amherst.edu)
 * @date   April 2022
 *
 * A network layer that perform routing via random link selection.
 */
public class RandomNetworkLayer extends NetworkLayer {
// =============================================================================



    // =========================================================================
    // PUBLIC METHODS
    // =========================================================================



    // =========================================================================
    /**
     * Default constructor.  Set up the random number generator.
     */
    public RandomNetworkLayer () {

	random = new Random();

    } // RandomNetworkLayer ()
    // =========================================================================

    

    // =========================================================================
    /**
     * Create a single packet containing the given data, with header that marks
     * the source and destination hosts.
     *
     * @param destination The address to which this packet is sent.
     * @param data        The data to send.
     * @return the sequence of bytes that comprises the packet.
     */
    protected byte[] createPacket (int destination, byte[] data) {

	// COMPLETE ME
		// Different parts of the packet being made
		byte[] len         = intToBytes(data.length);
		byte[] sour        = intToBytes(address);
		byte[] dest        = intToBytes(destination);
		
		byte[] packet      = new byte[heading + data.length];
		
		// Copying components of packet into the actual packet
		copyInto(packet, lengthOffset, len);
		copyInto(packet, sourceOffset, sour);
		copyInto(packet, destinationOffset, dest);
		copyInto(packet, heading, data);
	
		
		return packet; 
    } // createPacket ()
    // =========================================================================



    // =========================================================================
    /**
     * Randomly choose the link through which to send a packet given its
     * destination.
     *
     * @param destination The address to which this packet is being sent.
     */
    protected DataLinkLayer route (int destination) {

	// COMPLETE ME
		if (dataLinkLayers.containsKey(destination)) {
			return dataLinkLayers.get(destination);
		} else {
			// Create an array of the keys and get a random key to get a DL layer
			Object[] links = dataLinkLayers.keySet().toArray();
			Object addr = links[random.nextInt(links.length)];
			return dataLinkLayers.get(addr);
		}
	
    } // route ()
    // =========================================================================



    // =========================================================================
    /**
     * Examine a buffer to see if it's data can be extracted as a packet; if so,
     * do it, and return the packet whole.
     *
     * @param buffer The receive-buffer to be examined.
     * @return the packet extracted packet if a whole one is present in the
     *         buffer; <code>null</code> otherwise.
     */
    protected byte[] extractPacket (Queue<Byte> buffer) {

	// COMPLETE ME
		// If not of sufficient size, then return null
		if (buffer.size() < 13) {
			return null;
		} 
		
		// Create a copy of the buffer as an array list
		ArrayList<Byte> bufferCopy = new ArrayList<>(buffer);
		byte[] packetLen = new byte[Integer.BYTES];
		
		// Copy first 4 bytes to get the length of data
		for(int i = 0; i < packetLen.length; i++){
			packetLen[i] = bufferCopy.get(i);
		}
		
		// If the size of the full packet is larger than what is in the buffer,
		// then return null
		int pSize = bytesToInt(packetLen);
		if(buffer.size() < pSize + heading){
			return null;
		}
		
		// Now extract the bytes from buffer itself into an array we will return
		byte[] extractedBytes = new byte[pSize + heading];
		for(int i = 0; i < extractedBytes.length; i++){
			extractedBytes[i] = buffer.remove();
		}
		
		return extractedBytes;
	
    } // extractPacket ()
    // =========================================================================
	
	

    // =========================================================================
    /**
     * Given a received packet, process it.  If the destination for the packet
     * is this host, then deliver its data to the client layer.  If the
     * destination is another host, route and send the packet.
     *
     * @param packet The received packet to process.
     * @see   createPacket
     */
    protected void processPacket (byte[] packet) {

	// COMPLETE ME
		// Get the destination number from the packet
		byte[] dest = new byte[Integer.BYTES];
		copyFrom(dest,packet,destinationOffset);
			
		int destNum = bytesToInt(dest);
		
		// Check the destination number with host's address
		if (destNum == address) {
			// Extract the data from the packet and receive
			byte [] toDeliver = new byte[packet.length-12];
			for(int i = 0; i < toDeliver.length; i++){
				toDeliver[i] = packet[i+12];
			}
			client.receive(toDeliver);
		} else {
			// Send the packet to random DL layer
			DataLinkLayer dataLink = route(destNum);
			dataLink.send(packet);
		}
	
    } // processPacket ()
    // =========================================================================
    


    // =========================================================================
    // INSTANCE DATA MEMBERS

    /** The random source for selecting routes. */
    private Random random;
    // =========================================================================



    // =========================================================================
    // CLASS DATA MEMBERS

    /** The offset into the header for the length. */
    public static final int     lengthOffset      = 0;

    /** The offset into the header for the source address. */
    public static final int     sourceOffset      = lengthOffset + Integer.BYTES;

    /** The offset into the header for the destination address. */
    public static final int     destinationOffset = sourceOffset + Integer.BYTES;
	
	/** The offset into the header for the data. */
	public static final int     heading           = destinationOffset + Integer.BYTES;

    /** Whether to emit debugging information. */
    public static final boolean debug             = false;
   // =========================================================================


    
// =============================================================================
} // class RandomNetworkLayer
// =============================================================================
