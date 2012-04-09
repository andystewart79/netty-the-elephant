package uk.co.bssd.netty;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DisconnectListeners implements Iterable<DisconnectListener>{

	private final Set<DisconnectListener> listeners;
	
	public DisconnectListeners() {
		this.listeners = new HashSet<DisconnectListener>();
	}
	
	@Override
	public Iterator<DisconnectListener> iterator() {
		return this.listeners.iterator();
	}

	public void addDisconnectListener(DisconnectListener listener) {
		this.listeners.add(listener);
	}
}