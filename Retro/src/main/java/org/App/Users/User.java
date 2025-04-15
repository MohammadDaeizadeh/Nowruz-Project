package org.App.Users;

import org.App.Account.Account;
import java.util.*;

public class User extends Account {
    private final Set<Integer> followedArtistIds = new HashSet<>();
    private final Set<Integer> likedSongIds = new HashSet<>();

    public User(int id, String username, String email, String password, int age) {
        super(id, username, email, password, age);
    }

    public void followArtists(int artistId) {
        if (followedArtistIds.contains(artistId)) {
            followedArtistIds.remove(artistId);
        } else {
            followedArtistIds.add(artistId);
        }
    }

    public void likeSong(int songId) {
        if (likedSongIds.contains(songId)) {
            likedSongIds.remove(songId);
        } else {
            likedSongIds.add(songId);
        }
    }

    public boolean isLikedBy(int songId) {
        return likedSongIds.contains(songId);
    }

    public boolean hasLikedSong(int songId) {
        return likedSongIds.contains(songId);
    }

    public boolean isFollowingArtist(int artistId) {
        return followedArtistIds.contains(artistId);
    }

    public Set<Integer> getFollowedArtists() {
        return Collections.unmodifiableSet(followedArtistIds);
    }

    public Set<Integer> getLikedSongs() {
        return Collections.unmodifiableSet(likedSongIds);
    }

    @Override
    public String getAccountType() {
        return "User";
    }
}