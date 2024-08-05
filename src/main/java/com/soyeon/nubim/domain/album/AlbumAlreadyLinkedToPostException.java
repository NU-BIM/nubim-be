package com.soyeon.nubim.domain.album;

public class AlbumAlreadyLinkedToPostException extends IllegalStateException {
	public AlbumAlreadyLinkedToPostException() {
		super("This album is already linked to a post.");
	}
}
