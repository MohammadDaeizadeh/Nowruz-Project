package org.App.contents;


import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private final int id;
    private final int userId;
    private final int songId;
    private String text;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isEdited;

    public Comment(int id, int userId, int songId, String text) {
        this.id = id;
        this.userId = userId;
        this.songId = songId;
        this.text = validateText(text);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.isEdited = false;
    }

    //check comment validation
    private String validateText(String text) {
        Objects.requireNonNull(text, "Comment text cannot be null");
        text = text.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("Comment cannot exceed 500 characters");
        }
        return text;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getSongId() { return songId; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isEdited() { return isEdited; }

    // Edit comment
    public void updateText(String newText) {
        this.text = validateText(newText);
        this.updatedAt = LocalDateTime.now();
        this.isEdited = true;
    }

    @Override
    public String toString() {
        return String.format("Comment[id=%d, user=%d, song=%d, text='%s']",
                id, userId, songId, text);
    }
}