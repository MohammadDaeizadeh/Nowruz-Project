package org.App.contents;


import java.time.LocalDate;
import java.util.*;

public class Song {
    private final int id;
    private final String title;
    private final Integer albumId; // null for singles
    private final Set<Integer> artistIds;
    private String lyrics;
    private final String genre;
    private final Set<String> tags;
    private int views;
    private int likes;
    private final List<Comment> comments;
    private final Set<Integer> likedByUsers;
    private final LocalDate releaseDate;

    public Song(int id, String title, Integer albumId, Set<Integer> artistIds,
                String lyrics, String genre, Set<String> tags) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.albumId = albumId;
        this.artistIds = new HashSet<>(Objects.requireNonNull(artistIds));
        this.lyrics = Objects.requireNonNull(lyrics);
        this.genre = Objects.requireNonNull(genre);
        this.tags = new HashSet<>(Objects.requireNonNull(tags));
        this.views = 0;
        this.likes = 0;
        this.comments = new ArrayList<>();
        this.likedByUsers = new HashSet<>();
        this.releaseDate = LocalDate.now();
    }

    // Getters and business methods
    public int getId() { return id; }
    public String getTitle() { return title; }
    public Optional<Integer> getAlbumId() { return Optional.ofNullable(albumId); }
    public Set<Integer> getArtistIds() { return Collections.unmodifiableSet(artistIds); }
    public String getLyrics() { return lyrics; }
    public String getGenre() { return genre; }
    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }
    public int getViews() { return views; }
    public int getLikes() { return likes; }
    public LocalDate getReleaseDate() { return releaseDate; }

    public void updateLyrics(String newLyrics) {
        this.lyrics = Objects.requireNonNull(newLyrics);
    }

    public void addComment(Comment comment) {
        Objects.requireNonNull(comment, "Comment cannot be null");
        if (comment.getSongId() != this.id) {
            throw new IllegalArgumentException("Comment does not belong to this song");
        }
        comments.add(comment);
    }

    public boolean removeComment(int commentId, int requestingUserId) {
        Comment comment = findCommentById(commentId);
        if (comment == null) return false;

        // just user or admin can delete comment
        if (comment.getUserId() != requestingUserId /* && !userIsAdmin */) {
            throw new SecurityException("Not authorized to delete this comment");
        }

        return comments.remove(comment);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public Optional<Comment> getCommentById(int commentId) {
        return Optional.ofNullable(findCommentById(commentId));
    }

    private Comment findCommentById(int commentId) {
        return comments.stream()
                .filter(c -> c.getId() == commentId)
                .findFirst()
                .orElse(null);
    }

    public void like(int userId) {
        likedByUsers.add(userId);
        this.likes = likedByUsers.size();
    }

    public void unlike(int userId) {
        likedByUsers.remove(userId);
        this.likes = likedByUsers.size();
    }

    public boolean isLikedBy(int userId) {
        return likedByUsers.contains(userId);
    }

    public void incrementViews() { views++; }


}