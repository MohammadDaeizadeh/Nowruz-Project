package org.App.Users;

import org.App.Account.Account;
import org.App.contents.Album;
import org.App.contents.Song;
import java.util.*;

public class Artist extends Account {
    private boolean isApproved = true; // Default to false requiring admin approval
    private final Set<Integer> artistFollowersIds = new HashSet<>();
    private final Set<Integer> songIds = new HashSet<>();
    public final Set<Integer> albumIds = new HashSet<>();

    public Artist(int id, String username, String email, String password, int age) {
        super(id, username, email, password, age);
    }

    // Approval status
    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }

    // Song management
    public void addSong(Song song) {
        Objects.requireNonNull(song, "Song cannot be null");
        if (!song.getArtistIds().contains(this.getId())) {
            throw new IllegalArgumentException("Song does not belong to this artist");
        }
        songIds.add(song.getId());
    }

    public void removeSong(int songId) {
        songIds.remove(songId);
    }

    public Set<Integer> getSongIds() {
        return Collections.unmodifiableSet(songIds);
    }

    // Album management
    public void addAlbum(Album album) {
        Objects.requireNonNull(album, "Album cannot be null");
        if (!album.getArtistIds().contains(this.getId())) {
            throw new IllegalArgumentException("Album does not belong to this artist");
        }
        albumIds.add(album.getId());
    }

    public void removeAlbum(int albumId) {
        albumIds.remove(albumId);
    }

    public Set<Integer> getAlbumIds() {
        return Collections.unmodifiableSet(albumIds);
    }

    // Follower management
    public boolean addFollower(int followerId) {
        if (followerId <= 0) {
            throw new IllegalArgumentException("Follower ID must be positive");
        }
        return artistFollowersIds.add(followerId);
    }

    public boolean removeFollower(int followerId) {
        return artistFollowersIds.remove(followerId);
    }

    public int getFollowerCount() {
        return artistFollowersIds.size();
    }

    public Set<Integer> getFollowers() {
        return Collections.unmodifiableSet(artistFollowersIds);
    }

    public boolean isFollowedBy(int userId) {
        return artistFollowersIds.contains(userId);
    }

    // Deprecated methods (keep for backward compatibility)
    @Deprecated
    public void toggleSong(int songId) {
        if (songIds.contains(songId)) {
            songIds.remove(songId);
        } else {
            songIds.add(songId);
        }
    }

    @Deprecated
    public void toggleFollower(int followerId) {
        if (artistFollowersIds.contains(followerId)) {
            artistFollowersIds.remove(followerId);
        } else {
            artistFollowersIds.add(followerId);
        }
    }

    @Override
    public String getAccountType() {
        return "Artist";
    }
}