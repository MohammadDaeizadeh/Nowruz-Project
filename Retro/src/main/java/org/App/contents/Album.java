package org.App.contents;

import java.time.LocalDate;
import java.util.*;

public class Album {
    private final int id;
    private final String title;
    private final Set<Integer> artistIds;
    private final Set<Integer> trackIds = new HashSet<>();
    private final LocalDate releaseDate;
    private final String coverArtUrl;
    private String description;
    private final Set<String> genres = new HashSet<>();

    public Album(int id, String title, Set<Integer> artistIds, String coverArtUrl) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.artistIds = new HashSet<>(Objects.requireNonNull(artistIds));
        this.releaseDate = LocalDate.now();
        this.coverArtUrl = coverArtUrl;
        this.description = "";
    }

    // Core methods
    public void addTrack(Song song) {
        if (song.getAlbumId().isEmpty() || song.getAlbumId().get() != this.id) {
            throw new IllegalArgumentException("Song doesn't belong to this album");
        }
        trackIds.add(song.getId());
    }

    public void removeTrack(int songId) {
        trackIds.remove(songId);
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public Set<Integer> getArtistIds() { return Collections.unmodifiableSet(artistIds); }
    public Set<Integer> getTrackIds() { return Collections.unmodifiableSet(trackIds); }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getCoverArtUrl() { return coverArtUrl; }
    public String getDescription() { return description; }
    public Set<String> getGenres() { return Collections.unmodifiableSet(genres); }

    // Setters
    public void setDescription(String description) {
        this.description = Objects.requireNonNullElse(description, "");
    }

    public boolean addGenre(String genre) {
        return genre != null && genres.add(genre.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("Album[id=%d, title='%s', artists=%s, tracks=%d]",
                id, title, artistIds, trackIds.size());
    }
}