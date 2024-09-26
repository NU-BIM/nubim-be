package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.album.dto.AlbumRequestDto;
import com.soyeon.nubim.domain.album.exception.AlbumNotFoundException;
import com.soyeon.nubim.domain.album.exception.InvalidCoordinateException;
import com.soyeon.nubim.domain.album.exception.UnauthorizedAlbumAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumValidator {

	private final AlbumRepository albumRepository;
	private static final int COORDINATE_DIMENSION = 2;

	public void validateAlbumOwner(Long albumId, Long userId) {
		Long albumOwnerId = albumRepository.findAlbumOwnerIdByAlbumId(albumId)
			.orElseThrow(() -> new AlbumNotFoundException(albumId));

		if (!albumOwnerId.equals(userId)) {
			throw new UnauthorizedAlbumAccessException(albumOwnerId);
		}
	}

	public void validateAlbumPathCoordinates(AlbumRequestDto albumRequestDto) {
		List<List<Double>> path = albumRequestDto.getPath();
		for (List<Double> coordinate : path) {
			int dimension = coordinate.size();
			if (dimension != COORDINATE_DIMENSION) {
				throw new InvalidCoordinateException(dimension);
			}
		}
	}
}
