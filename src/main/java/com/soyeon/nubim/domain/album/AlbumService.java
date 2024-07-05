package com.soyeon.nubim.domain.album;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AlbumService {

	private AlbumRepository albumRepository;

	public Optional<Album> findById(Long id) {
		return albumRepository.findById(id);
	}
}
