package com.soyeon.nubim.domain.album;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.album.exception.UnauthorizedAlbumAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumValidator {

	private final AlbumRepository albumRepository;

	public void validateAlbumOwner(Long albumId, Long userId) {
		Long albumOwnerId = albumRepository.findAlbumOwnerIdByAlbumId(albumId);

		if (!albumOwnerId.equals(userId)) {
			throw new UnauthorizedAlbumAccessException(albumOwnerId);
		}
	}
}
