package org.App.Users;

import org.App.Account.Account;
import org.App.contents.Album;
import org.App.contents.Song;
import org.App.contents.Comment;
import java.time.LocalDateTime;
import java.util.*;

public class Admin extends Account {
    public enum ContentType { SONG, ALBUM, COMMENT, USER, ARTIST }
    public enum ActionType {
        APPROVAL, REMOVAL,
        SUSPENSION, RESTORATION,
        BAN, WARNING
    }

    private final Map<Integer, Album> albums;
    private final Set<Song> songs;
    private final Map<String, Account> accounts;

    public Admin(int id, String username, String email, String password, int age,
                 Map<Integer, Album> albums, Set<Song> songs, Map<String, Account> accounts) {
        super(id, username, email, password, age);
        this.albums = albums;
        this.songs = songs;
        this.accounts = accounts;
    }

    // Content Moderation Methods
    public boolean approveArtist(int artistId, String approvalReason) {
        try {
            Artist artist = getArtistById(artistId);
            artist.setApproved(true);
            logAction(artistId, ContentType.ARTIST, ActionType.APPROVAL, approvalReason);
            return true;
        } catch (Exception e) {
            logError("approveArtist", e);
            return false;
        }
    }

    public boolean removeContent(int contentId, ContentType contentType, String reason) {
        try {
            switch (contentType) {
                case SONG:
                    return deleteSong(contentId, reason);
                case ALBUM:
                    return deleteAlbum(contentId, reason);
                case COMMENT:
                    return deleteComment(contentId, reason);
                default:
                    throw new IllegalArgumentException("Unsupported content type");
            }
        } catch (Exception e) {
            logError("removeContent", e);
            return false;
        }
    }

    // Account Management Methods
    public boolean suspendAccount(int accountId, String reason) {
        try {
            Account account = getAccountById(accountId);
            account.deactivate();
            logAction(accountId, ContentType.USER, ActionType.SUSPENSION, reason);
            return true;
        } catch (Exception e) {
            logError("suspendAccount", e);
            return false;
        }
    }

    public boolean banAccount(int accountId, String reason) {
        try {
            Account account = getAccountById(accountId);
            account.ban();
            logAction(accountId, null, ActionType.BAN, reason);
            return true;
        } catch (Exception e) {
            logError("banAccount", e);
            return false;
        }
    }

    public boolean restoreAccount(int accountId, String reason) {
        try {
            Account account = getAccountById(accountId);
            account.unban();
            logAction(accountId, null, ActionType.RESTORATION, reason);
            return true;
        } catch (Exception e) {
            logError("restoreAccount", e);
            return false;
        }
    }

    // Specific Content Deletion Methods
    public boolean deleteSong(int songId, String reason) {
        try {
            Song song = getSongById(songId);

            // Remove from all albums first
            for (Album album : albums.values()) {
                if (album.getTrackIds().contains(songId)) {
                    album.removeTrack(songId);
                }
            }

            // Remove from artists' collections
            for (Account account : accounts.values()) {
                if (account instanceof Artist) {
                    Artist artist = (Artist) account;
                    if (artist.getSongIds().contains(songId)) {
                        artist.removeSong(songId);
                    }
                }
            }

            // Finally remove from global collection
            songs.removeIf(s -> s.getId() == songId);

            logAction(songId, ContentType.SONG, ActionType.REMOVAL, reason);
            return true;
        } catch (Exception e) {
            logError("deleteSong", e);
            return false;
        }
    }

    private boolean deleteAlbum(int albumId, String reason) {
        Album album = getAlbumById(albumId);

        // Remove all songs
        new ArrayList<>(songs).stream()
                .filter(s -> s.getAlbumId().isPresent() &&
                        s.getAlbumId().get() == albumId)
                .forEach(s -> deleteSong(s.getId(), "Album deletion cascade"));

        // Remove from artists
        accounts.values().stream()
                .filter(a -> a instanceof Artist)
                .map(a -> (Artist) a)
                .forEach(artist -> artist.removeAlbum(albumId));

        albums.remove(albumId);
        logAction(albumId, ContentType.ALBUM, ActionType.REMOVAL, reason);
        return true;
    }

    private boolean deleteComment(int commentId, String reason) {
        // Search all songs for the comment
        for (Song song : songs) {
            Optional<Comment> comment = song.getCommentById(commentId);
            if (comment.isPresent()) {
                song.removeComment(commentId, this.getId());
                logAction(commentId, ContentType.COMMENT, ActionType.REMOVAL, reason);
                return true;
            }
        }
        throw new IllegalArgumentException("Comment not found");
    }

    // Helper Methods
    private void removeArtistContent(Artist artist) {
        // Remove albums
        new ArrayList<>(albums.values()).stream()
                .filter(a -> a.getArtistIds().contains(artist.getId()))
                .forEach(a -> deleteAlbum(a.getId(), "Artist ban cascade"));

        // Remove singles
        new ArrayList<>(songs).stream()
                .filter(s -> s.getArtistIds().contains(artist.getId()) &&
                        s.getAlbumId().isEmpty())
                .forEach(s -> deleteSong(s.getId(), "Artist ban cascade"));
    }

    private Account getAccountById(int accountId) {
        return accounts.values().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private Artist getArtistById(int artistId) {
        Account account = getAccountById(artistId);
        if (!(account instanceof Artist)) {
            throw new IllegalArgumentException("Account is not an artist");
        }
        return (Artist) account;
    }

    private Song getSongById(int songId) {
        return songs.stream()
                .filter(s -> s.getId() == songId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
    }

    private Album getAlbumById(int albumId) {
        Album album = albums.get(albumId);
        if (album == null) {
            throw new IllegalArgumentException("Album not found");
        }
        return album;
    }

    private void logAction(int targetId, ContentType contentType, ActionType actionType, String reason) {
        String logMessage = String.format(
                "Admin %d (%s) performed %s on %s %d. Reason: %s. Timestamp: %s",
                this.getId(),
                this.getUsername(),
                actionType,
                contentType != null ? contentType : "account",
                targetId,
                reason,
                LocalDateTime.now()
        );
        System.out.println("[ADMIN ACTION] " + logMessage);
        // In production: write to database or log file
    }

    private void logError(String operation, Exception e) {
        System.err.printf("Admin %d failed %s: %s%n",
                this.getId(), operation, e.getMessage());
    }



    @Override
    public String getAccountType() {
        return "Admin";
    }
}