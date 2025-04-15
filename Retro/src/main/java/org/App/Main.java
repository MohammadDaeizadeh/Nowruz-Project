package org.App;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import org.App.Account.Account;
import org.App.contents.Song;
import org.App.contents.Album;
import org.App.Users.User;
import org.App.Users.Admin;
import org.App.Users.Artist;
import org.App.contents.Comment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.swing.border.TitledBorder;

public class Main {
    private static final HashMap<String, Account> accounts = new HashMap<>();
    private static final Map<Integer, Album> albums = new HashMap<>();
    private static final Set<Song> songs = new HashSet<>();
    private static Account currentUser = null;

    public static void main(String[] args) {
        // Initialize some test data
        initializeTestData();
        // Create and show the login/register frame
        SwingUtilities.invokeLater(Main::createAndShowLoginFrame);
    }

    private static void initializeTestData() {
        // Add an admin
        accounts.put("admin@music.com", new Admin(1, "admin", "admin@music.com", "admin123", 30,
                albums, songs, accounts));

        // Add some pending artists
        Artist artist1 = new Artist(2, "artist1", "artist1@music.com", "artist123", 25);
        Artist artist2 = new Artist(3, "artist2", "artist2@music.com", "artist123", 28);
        accounts.put(artist1.getEmail(), artist1);
        accounts.put(artist2.getEmail(), artist2);

        // Add regular users
        accounts.put("user1@music.com", new User(4, "user1", "user1@music.com", "user1234", 20));
        accounts.put("user2@music.com", new User(5, "user2", "user2@music.com", "user1234", 22));

        // Create artist IDs set
        Set<Integer> artistIds = new HashSet<>();
        artistIds.add(artist1.getId());

        // Create albums map
        Album summerAlbum = new Album(1, "Summer Vibes", artistIds, "summer_cover.jpg");
        albums.put(summerAlbum.getId(), summerAlbum);

        // Add songs (keeping songs as Set)
        songs.addAll(Arrays.asList(
                // Singles
                new Song(1, "First Single", null, artistIds, "Lyrics here", "Pop", Set.of("hit", "summer")),
                new Song(2, "Second Single", null, artistIds, "More lyrics", "Rock", Set.of("rock", "guitar")),

                // Album tracks
                new Song(3, "Summer Breeze", summerAlbum.getId(), artistIds,
                        "Hot summer days...", "Pop", Set.of("summer", "chill")),
                new Song(4, "Ocean View", summerAlbum.getId(), artistIds,
                        "Waves crashing...", "Pop", Set.of("calm", "beach"))
        ));

        // Add tracks to album
        for (Song song : songs) {
            if (song.getAlbumId().isPresent()) {
                Album album = albums.get(song.getAlbumId().get());
                if (album != null) {
                    album.addTrack(song);
                }
            }
        }
    }

    private static void createAndShowLoginFrame() {
        JFrame frame = new JFrame("Retro Music App - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(new JLabel()); // Empty cell
        frame.add(loginButton);
        frame.add(new JLabel()); // Empty cell
        frame.add(registerButton);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();
            String password = new String(passwordField.getPassword());

            Account account = accounts.get(email);
            if (account != null && account.verifyPassword(password)) {
                currentUser = account;
                frame.dispose();
                showMainApp();
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            showRegistrationFrame();
        });

        frame.setVisible(true);
    }

    private static void showRegistrationFrame() {
        JFrame frame = new JFrame("Music App - Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();
        JLabel accountTypeLabel = new JLabel("Account Type:");
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"User", "Artist"});
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(accountTypeLabel);
        frame.add(accountTypeCombo);
        frame.add(backButton);
        frame.add(registerButton);

        registerButton.addActionListener(e -> {
            try {
                // Get input values
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim().toLowerCase(); // Normalize email
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                int age = Integer.parseInt(ageField.getText());
                String accountType = (String) accountTypeCombo.getSelectedItem();

                // Validate inputs
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (accounts.containsKey(email)) {
                    JOptionPane.showMessageDialog(frame, "Email already registered", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (age < Account.MIN_AGE) {
                    JOptionPane.showMessageDialog(frame,
                            "You must be at least " + Account.MIN_AGE + " years old",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < Account.MIN_PASSWORD_LENGTH) {
                    JOptionPane.showMessageDialog(frame,
                            "Password must be at least " + Account.MIN_PASSWORD_LENGTH + " characters",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create new account (password will be hashed in Account constructor)
                Account newAccount;
                if ("User".equals(accountType)) {
                    newAccount = new User(accounts.size() + 1, username, email, password, age);
                    JOptionPane.showMessageDialog(frame, "Registration successful!");
                } else {
                    newAccount = new Artist(accounts.size() + 1, username, email, password, age);
                    JOptionPane.showMessageDialog(frame, "Artist account created. Waiting for admin approval.");
                }

                // Store account and clear sensitive data
                accounts.put(email, newAccount);
                Arrays.fill(passwordChars, '\0'); // Clear password from memory
                password = null; // Remove password reference

                // Navigate to login
                frame.dispose();
                createAndShowLoginFrame();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Ensure password field is cleared regardless of outcome
                passwordField.setText("");
            }
        });



        backButton.addActionListener(e -> {
            frame.dispose();
            createAndShowLoginFrame();
        });

        frame.setVisible(true);
    }

    private static void showCreateAlbumDialog(Artist artist) {
        JFrame frame = new JFrame("Create New Album");
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField coverUrlField = new JTextField();
        JTextField genresField = new JTextField();

        inputPanel.add(new JLabel("Album Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JScrollPane(descriptionArea));
        inputPanel.add(new JLabel("Cover Art URL:"));
        inputPanel.add(coverUrlField);
        inputPanel.add(new JLabel("Genre:"));
        JComboBox<String> genreCombo = new JComboBox<>(new String[]{"Pop", "Rock", "Hip-Hop", "R&B", "Electronic", "Jazz",
                "Classical", "Country", "Metal", "Alternative"});
        inputPanel.add(genreCombo);


        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Album");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Album title is required");
                return;
            }

            Album album = new Album(
                    albums.size() + 1,
                    title,
                    Set.of(artist.getId()),
                    coverUrlField.getText().trim()
            );

            album.setDescription(descriptionArea.getText());
            Arrays.stream(genresField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(album::addGenre);

            albums.put(album.getId(), album);
            artist.addAlbum(album);
            JOptionPane.showMessageDialog(frame, "Album created successfully!");
            frame.dispose();
        });

        cancelButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void showAddTrackDialog(Album album, Artist artist) {
        JFrame frame = new JFrame("Add Track to " + album.getTitle());
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextArea lyricsArea = new JTextArea(3, 20);
        JComboBox<String> genreCombo = new JComboBox<>(new String[]{"Pop", "Rock", "Hip-Hop", "R&B", "Electronic", "Jazz",
                        "Classical", "Country", "Metal", "Alternative"});
        JTextField tagsField = new JTextField();

        frame.add(new JLabel("Track Title:"));
        frame.add(titleField);
        frame.add(new JLabel("Lyrics:"));
        frame.add(new JScrollPane(lyricsArea));
        frame.add(new JLabel("Genre:"));
        frame.add(genreCombo);
        frame.add(new JLabel("Tags (comma separated):"));
        frame.add(tagsField);

        JButton addButton = new JButton("Add Track");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Track title is required");
                return;
            }

            int newSongId = songs.size() + 1;
            Song song = new Song(
                    newSongId,
                    title,
                    album.getId(),
                    album.getArtistIds(),
                    lyricsArea.getText(),
                    (String)genreCombo.getSelectedItem(),
                    Arrays.stream(tagsField.getText().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toSet())
            );

            songs.add(song);
            artist.addSong(song);
            album.addTrack(song);
            JOptionPane.showMessageDialog(frame, "Track added successfully!");
            frame.dispose();
        });

        cancelButton.addActionListener(e -> frame.dispose());

        frame.add(addButton);
        frame.add(cancelButton);
        frame.setVisible(true);
    }

    private static void showAddSongDialog(Artist artist) {
        JFrame addSongFrame = new JFrame("Add New Song");
        addSongFrame.setSize(400, 400);
        addSongFrame.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();
        JLabel lyricsLabel = new JLabel("Lyrics:");
        JTextArea lyricsArea = new JTextArea();
        JScrollPane lyricsScroll = new JScrollPane(lyricsArea);
        JLabel genreLabel = new JLabel("Genre:");
        JComboBox<String> genreField = new JComboBox<>(new String[]{"Pop", "Rock", "Hip-Hop", "R&B", "Electronic", "Jazz",
                "Classical", "Country", "Metal", "Alternative"});
        JLabel tagsLabel = new JLabel("Tags (comma separated):");
        JTextField tagsField = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        addSongFrame.add(titleLabel);
        addSongFrame.add(titleField);
        addSongFrame.add(lyricsLabel);
        addSongFrame.add(lyricsScroll);
        addSongFrame.add(genreLabel);
        addSongFrame.add(genreField);
        addSongFrame.add(tagsLabel);
        addSongFrame.add(tagsField);
        addSongFrame.add(new JLabel()); // empty cell
        addSongFrame.add(submitButton);
        addSongFrame.add(new JLabel()); // empty cell
        addSongFrame.add(cancelButton);

        submitButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                String lyrics = lyricsArea.getText();
                String genre = (String) genreField.getSelectedItem();
                Set<String> tags = new HashSet<>(Arrays.asList(tagsField.getText().split(",")));

                // Validate inputs
                if (title.isEmpty() || lyrics.isEmpty() || genre.isEmpty()) {
                    throw new IllegalArgumentException("Please fill all required fields");
                }

                // Create new song
                int newSongId = songs.size() + 1;
                Set<Integer> artistIds = new HashSet<>();
                artistIds.add(artist.getId());

                Song newSong = new Song(newSongId, title, null, artistIds,
                        lyrics, genre, tags);
                songs.add(newSong);
                artist.addSong(newSong);

                JOptionPane.showMessageDialog(addSongFrame, "Song added successfully!");
                addSongFrame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addSongFrame, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addSongFrame.dispose());

        addSongFrame.setVisible(true);
    }

    private static void showArtistSongsPanel(Artist artist) {
        JFrame artistFrame = new JFrame(artist.getUsername() + "'s Songs");
        artistFrame.setSize(800, 600);
        artistFrame.setLayout(new BorderLayout(10, 10));

        // Theme colors
        Color primaryColor = new Color(70, 130, 180);
        Color accentColor = new Color(110, 110, 110);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));

        // Back button
        JButton backButton = createStyledButton("← Back", primaryColor);
        backButton.addActionListener(e -> artistFrame.dispose());
        headerPanel.add(backButton, BorderLayout.WEST);

        // Artist info
        JPanel artistInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        artistInfoPanel.add(new JLabel("Artist: " + artist.getUsername()));
        artistInfoPanel.add(new JLabel("Followers: " + artist.getFollowerCount()));
        headerPanel.add(artistInfoPanel, BorderLayout.CENTER);

        // Right side buttons panel
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Add Follow/Unfollow button for regular users
        if (currentUser instanceof User) {
            User user = (User) currentUser;
            JButton followButton = new JButton(
                    user.isFollowingArtist(artist.getId()) ? "Unfollow" : "Follow"
            );
            styleButton(followButton,
                    user.isFollowingArtist(artist.getId()) ? accentColor : primaryColor);

            followButton.addActionListener(e -> {
                user.followArtists(artist.getId()); // This toggles follow state
                if (user.isFollowingArtist(artist.getId())) {
                    artist.addFollower(user.getId());
                } else {
                    artist.removeFollower(user.getId());
                }
                // Update button text and color
                followButton.setText(user.isFollowingArtist(artist.getId()) ? "Unfollow" : "Follow");
                styleButton(followButton,
                        user.isFollowingArtist(artist.getId()) ? accentColor : primaryColor);
                // Update followers count
                artistInfoPanel.remove(1);
                artistInfoPanel.add(new JLabel("Followers: " + artist.getFollowerCount()));
                artistInfoPanel.revalidate();
                artistInfoPanel.repaint();
            });
            rightButtonsPanel.add(followButton);
        }

        // Add New Song button (for the artist themselves)
        if (currentUser instanceof Artist && currentUser.getId() == artist.getId()) {
            JButton addSongButton = createStyledButton("Add New Song ", new Color(70, 130, 180));
            addSongButton.addActionListener(e -> showAddSongDialog(artist));
            rightButtonsPanel.add(addSongButton);
        }

        headerPanel.add(rightButtonsPanel, BorderLayout.EAST);

        // Artist menu
        if (currentUser instanceof Artist && currentUser.getId() == artist.getId()) {
            JMenuBar menuBar = new JMenuBar();
            JMenu artistMenu = new JMenu("Artist");

            JMenuItem createAlbumItem = new JMenuItem("Create New Album");
            createAlbumItem.addActionListener(e -> showCreateAlbumDialog(artist));

            JMenuItem viewAlbumsItem = new JMenuItem("My Albums");
            viewAlbumsItem.addActionListener(e -> showArtistAlbumsPanel(artist));

            artistMenu.add(createAlbumItem);
            artistMenu.add(viewAlbumsItem);
            menuBar.add(artistMenu);
            artistFrame.setJMenuBar(menuBar);
        }

        artistFrame.add(headerPanel, BorderLayout.NORTH);

        // Songs list
        DefaultListModel<String> songsModel = new DefaultListModel<>();
        for (Song song : songs) {
            if (song.getArtistIds().contains(artist.getId())) {
                String songInfo = song.getTitle();
                if (song.getAlbumId().isPresent()) {
                    Album album = albums.get(song.getAlbumId().get());
                    songInfo += " (from: " + album.getTitle() + ")";
                } else {
                    songInfo += " (Single)";
                }
                songsModel.addElement(songInfo);
            }
        }

        JList<String> songsList = new JList<>(songsModel);
        songsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = songsList.locationToIndex(e.getPoint());
                    Song song = songs.stream()
                            .filter(s -> s.getArtistIds().contains(artist.getId()))
                            .skip(index)
                            .findFirst()
                            .orElse(null);
                    if (song != null) {
                        if (!(currentUser instanceof Admin)) {
                            song.incrementViews();
                        }
                        showSongDetails(song);
                    }
                }
            }
        });

        artistFrame.add(new JScrollPane(songsList), BorderLayout.CENTER);
        artistFrame.setVisible(true);
    }




    private static void showMainApp() {
        if (!checkAccess()) {
            createAndShowLoginFrame();
            return;
        }
        JFrame frame = new JFrame("Music App - Welcome " + currentUser.getUsername());
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Menu Bar with modern styling
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(70, 130, 180));

        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setForeground(new Color(199, 0, 57));
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        if (currentUser instanceof Admin) {
            JMenu adminMenu = new JMenu("Admin");
            JMenuItem approveArtistsItem = new JMenuItem("Approve Artists");
            adminMenu.add(approveArtistsItem);
            menuBar.add(adminMenu);

            approveArtistsItem.addActionListener(e -> showArtistApprovalDialog());
        }

        frame.setJMenuBar(menuBar);

        // Main Content - Only showing artists now
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        JLabel titleLabel = new JLabel("Artists", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Artists list
        DefaultListModel<String> artistsModel = new DefaultListModel<>();
        JList<String> artistsList = new JList<>(artistsModel);
        artistsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        artistsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        artistsList.setFixedCellHeight(40);

        // Populate with approved artists only
        for (Account account : accounts.values()) {
            if (account instanceof Artist && ((Artist) account).isApproved()) {
                Artist artist = (Artist) account;
                String artistInfo = "<html>" + artist.getUsername() +
                        " <font color='gray'>(" + artist.getFollowerCount() + " followers)</font></html>";
                artistsModel.addElement(artistInfo);
            }
        }

        // Double-click to view artist's songs
        artistsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = artistsList.locationToIndex(e.getPoint());
                    if (selectedIndex != -1) {
                        // Find the selected artist
                        Artist selectedArtist = (Artist) accounts.values().stream()
                                .filter(a -> a instanceof Artist && ((Artist) a).isApproved())
                                .skip(selectedIndex)
                                .findFirst()
                                .orElse(null);

                        if (selectedArtist != null) {
                            showArtistSongsPanel(selectedArtist);
                        }
                    }
                }
            }
        });

        mainPanel.add(new JScrollPane(artistsList), BorderLayout.CENTER);

        // Profile button at bottom
        if (!(currentUser instanceof Admin)) {
            JButton profileButton = createStyledButton("My Profile", new Color(70, 130, 180));
            profileButton.setBackground(new Color(70, 130, 180));
            profileButton.addActionListener(e -> showProfilePanel());
            mainPanel.add(profileButton, BorderLayout.SOUTH);
        }

        if (currentUser instanceof Admin) {
            JMenuItem moderationItem = new JMenuItem("Moderation Panel");
            moderationItem.addActionListener(e -> showAdminModerationPanel());
            menuBar.add(moderationItem);
        }


        frame.add(mainPanel, BorderLayout.CENTER);

        logoutItem.addActionListener(e -> {
            currentUser = null;
            frame.dispose();
            createAndShowLoginFrame();
        });

        frame.setVisible(true);
    }

    private static void showProfilePanel() {
        JFrame profileFrame = new JFrame("My Profile");
        profileFrame.setSize(500, 500);
        profileFrame.setLayout(new BorderLayout(10, 10));

        // Theme colors
        Color primaryColor = new Color(70, 130, 180);
        Color secondaryColor = new Color(240, 248, 255);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backButton = createStyledButton("← Back", primaryColor);
        backButton.addActionListener(e -> profileFrame.dispose());
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("My Profile", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Profile content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Basic info
        addProfileRow(contentPanel, "Username:", currentUser.getUsername(), primaryColor);
        addProfileRow(contentPanel, "Email:", currentUser.getEmail(), primaryColor);

        // Bio section in JPanel
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.setBorder(BorderFactory.createTitledBorder("Bio"));

        JLabel bioLabel = new JLabel("<html><div style='padding:5px;'>" +
                (currentUser.getBio() != null ? currentUser.getBio() : "No bio yet") +
                "</div></html>");
        bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bioPanel.add(bioLabel, BorderLayout.CENTER);

        // Edit bio button
        JButton editBioButton = new JButton("Edit Bio");
        editBioButton.addActionListener(e -> {
            String newBio = JOptionPane.showInputDialog(profileFrame, "Enter your bio:", currentUser.getBio());
            if (newBio != null) {
                currentUser.setBio(newBio);
                bioLabel.setText("<html><div style='padding:5px;'>" + newBio + "</div></html>");
            }
        });
        bioPanel.add(editBioButton, BorderLayout.SOUTH);

        contentPanel.add(bioPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Account-specific info
        if (currentUser instanceof User) {
            User user = (User) currentUser;
            addProfileRow(contentPanel, "Following:", user.getFollowedArtists().size() + " artists", primaryColor);
            addProfileRow(contentPanel, "Liked Songs:", user.getLikedSongs().size() + " songs", primaryColor);
        } else if (currentUser instanceof Artist) {
            Artist artist = (Artist) currentUser;
            addProfileRow(contentPanel, "Followers:", artist.getFollowerCount() + "", primaryColor);
            addProfileRow(contentPanel, "Albums:", artist.albumIds.size() + "", primaryColor);
            addProfileRow(contentPanel, "Songs:", artist.getSongIds().size() + "", primaryColor);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        profileFrame.add(mainPanel);
        profileFrame.setVisible(true);
    }

    // Helper methods
    private static JLabel addInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        rowPanel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelComp.setForeground(new Color(70, 130, 180));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("SansSerif", Font.PLAIN, 14));

        rowPanel.add(labelComp);
        rowPanel.add(valueComp);
        panel.add(rowPanel);

        return valueComp;  // Return the value label so we can update it later
    }

    private static void addProfileRow(JPanel panel, String label, String value, Color color) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelLbl.setForeground(color);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        rowPanel.add(labelLbl);
        rowPanel.add(valueLbl);
        panel.add(rowPanel);
    }

    private static void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private static void showArtistApprovalDialog() {
        if (!(currentUser instanceof Admin)) return;

        JFrame approvalFrame = new JFrame("Approve Artists");
        approvalFrame.setSize(500, 400);
        approvalFrame.setLayout(new BorderLayout());

        DefaultListModel<String> pendingArtistsModel = new DefaultListModel<>();
        List<Artist> pendingArtists = new ArrayList<>();

        for (Account account : accounts.values()) {
            if (account instanceof Artist && !((Artist) account).isApproved()) {
                Artist artist = (Artist) account;
                pendingArtistsModel.addElement(artist.getUsername() + " (" + artist.getEmail() + ")");
                pendingArtists.add(artist);
            }
        }

        JList<String> pendingArtistsList = new JList<>(pendingArtistsModel);
        approvalFrame.add(new JScrollPane(pendingArtistsList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        approveButton.setForeground(new Color(31,125,83));
        rejectButton.setForeground(new Color(199, 0, 57));

        approveButton.addActionListener(e -> {
            int selectedIndex = pendingArtistsList.getSelectedIndex();
            if (selectedIndex != -1) {
                Artist artist = pendingArtists.get(selectedIndex);
                artist.setApproved(true);
                pendingArtistsModel.remove(selectedIndex);
                pendingArtists.remove(selectedIndex);
                JOptionPane.showMessageDialog(approvalFrame,
                        "Artist " + artist.getUsername() + " approved!");
            }
        });

        rejectButton.addActionListener(e -> {
            int selectedIndex = pendingArtistsList.getSelectedIndex();
            if (selectedIndex != -1) {
                Artist artist = pendingArtists.get(selectedIndex);
                accounts.remove(artist.getEmail());
                pendingArtistsModel.remove(selectedIndex);
                pendingArtists.remove(selectedIndex);
                JOptionPane.showMessageDialog(approvalFrame,
                        "Artist " + artist.getUsername() + " rejected and removed.");
            }
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        approvalFrame.add(buttonPanel, BorderLayout.SOUTH);
        approvalFrame.setVisible(true);
    }

    private static void showAlbumView(Album album, Artist artist) {
        JFrame frame = new JFrame("Album: " + album.getTitle());
        frame.setSize(700, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backButton = createStyledButton("← Back", new Color(70, 130, 180));
        backButton.addActionListener(e -> frame.dispose());
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(album.getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        if (currentUser instanceof Artist && album.getArtistIds().contains(((Artist)currentUser).getId())) {
            JButton addTrackButton = createStyledButton("Add Track", new Color(70, 130, 180));
            addTrackButton.addActionListener(e -> showAddTrackDialog(album, artist));
            headerPanel.add(addTrackButton, BorderLayout.EAST);
        }


        JButton deleteAlbumButton = createStyledButton("Delete Album", new Color(199, 0, 57));
        deleteAlbumButton.setForeground(new Color(199, 0, 57));
        deleteAlbumButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this entire album?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Remove album from artist's collection
                artist.removeAlbum(album.getId());

                // Remove all album tracks from global songs set
                songs.removeIf(s -> s.getAlbumId().isPresent() &&
                        s.getAlbumId().get() == album.getId());

                // Remove album from global albums map
                albums.remove(album.getId());

                JOptionPane.showMessageDialog(frame, "Album deleted successfully!");
                frame.dispose();
                showArtistAlbumsPanel(artist);
            }
        });
        frame.add(deleteAlbumButton, BorderLayout.SOUTH);


        // Album Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (!album.getCoverArtUrl().isEmpty()) {
            JLabel coverLabel = new JLabel(new ImageIcon(album.getCoverArtUrl()));
            infoPanel.add(coverLabel);
        }

        infoPanel.add(new JLabel("Released: " + album.getReleaseDate()));
        infoPanel.add(new JLabel("Artists: " + getArtistNames(album.getArtistIds())));
        infoPanel.add(new JLabel("Genres: " + album.getGenres()));

        JTextArea descriptionArea = new JTextArea(album.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(descriptionArea));

        // Tracks Panel
        DefaultListModel<String> tracksModel = new DefaultListModel<>();
        JList<String> tracksList = new JList<>(tracksModel);

        // Track counter for display
        AtomicInteger trackNumber = new AtomicInteger(1);

        // Get all songs from the album
        album.getTrackIds().stream()
                .map(songId -> songs.stream()
                        .filter(s -> s.getId() == songId)
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .forEach(song -> {
                    tracksModel.addElement(
                            String.format("%d. %s (%s)",
                                    trackNumber.getAndIncrement(),
                                    song.getTitle(),
                                    song.getGenre())
                    );
                });

        tracksList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = tracksList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        // Get the song ID from the album's track list
                        int songId = album.getTrackIds().stream()
                                .skip(index)
                                .findFirst()
                                .orElse(-1);

                        if (songId != -1) {
                            // Find the song in the Set
                            songs.stream()
                                    .filter(s -> s.getId() == songId)
                                    .findFirst()
                                    .ifPresent(Main::showSongDetails);
                        }
                    }
                }
            }
        });

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(infoPanel, BorderLayout.WEST);
        frame.add(new JScrollPane(tracksList), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void showArtistAlbumsPanel(Artist artist) {
        JFrame frame = new JFrame(artist.getUsername() + "'s Albums");
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        DefaultListModel<String> albumsModel = new DefaultListModel<>();
        JList<String> albumsList = new JList<>(albumsModel);

        albums.values().stream()
                .filter(album -> album.getArtistIds().contains(artist.getId()))
                .forEach(album -> albumsModel.addElement(
                        String.format("%s (%d tracks)", album.getTitle(), album.getTrackIds().size())
                ));

        albumsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = albumsList.locationToIndex(e.getPoint());
                    Album album = albums.values().stream()
                            .filter(a -> a.getArtistIds().contains(artist.getId()))
                            .skip(index)
                            .findFirst()
                            .orElse(null);
                    if (album != null) {
                        showAlbumView(album, artist);
                    }
                }
            }
        });

        frame.add(new JScrollPane(albumsList), BorderLayout.CENTER);

        JButton backButton = createStyledButton("← Back", new Color(70, 130, 180));
        backButton.addActionListener(e -> frame.dispose());
        frame.add(backButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Helper method to get artist names
    private static String getArtistNames(Set<Integer> artistIds) {
        return artistIds.stream()
                .map(id -> accounts.values().stream()
                        .filter(a -> a.getId() == id && a instanceof Artist)
                        .findFirst()
                        .map(Account::getUsername)
                        .orElse("Unknown Artist"))
                .collect(Collectors.joining(", "));
    }

    private static void showSongDetails(Song song) {
        JFrame detailsFrame = new JFrame("Song Details: " + song.getTitle());
        detailsFrame.setSize(650, 550);

        // Theme colors
        Color primaryColor = new Color(70, 130, 180);  // Steel blue
        Color secondaryColor = new Color(245, 245, 255);  // Light ivory
        Color accentColor = new Color(199, 0, 57);  // Crimson red

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(230, 240, 255),
                        getWidth(), getHeight(), new Color(255, 240, 245));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backButton = createStyledButton("← Back", new Color(70, 130, 180));
        backButton.addActionListener(e -> detailsFrame.dispose());
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(song.getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);


        JButton deleteButton = createStyledButton("Delete Song", new Color(199, 0, 57));
        deleteButton.setForeground(accentColor);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(detailsFrame,
                    "Are you sure you want to delete this song?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Artist artist = (Artist) currentUser;

                // Remove song from artist's collection
                artist.removeSong(song.getId());

                // Remove song from global songs set
                songs.removeIf(s -> s.getId() == song.getId());

                // Remove song from any albums it belongs to
                for (Album album : albums.values()) {
                    album.removeTrack(song.getId());
                }

                JOptionPane.showMessageDialog(detailsFrame, "Song deleted successfully!");
                detailsFrame.dispose();
                showArtistSongsPanel(artist);
            }
        });
        headerPanel.add(deleteButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Info panel
        JPanel infoPanel = createStyledPanel("Song Info", new Color(70, 130, 180));
        infoPanel.setLayout(new GridLayout(0, 2, 5, 5));

        // Album info
        Optional<Album> albumOpt = song.getAlbumId().map(albums::get);
        JLabel albumLabel = new JLabel(albumOpt.isPresent() ?
                "From Album: " + albumOpt.get().getTitle() : "Single Release");
        addInfoRow(infoPanel, "Album:", albumLabel.getText(), new Color(70, 130, 180));

        // Other info rows
        addInfoRow(infoPanel, "Genre:", song.getGenre(), new Color(70, 130, 180));
        JLabel likesLabel = addInfoRow(infoPanel, "Likes:", String.valueOf(song.getLikes()), new Color(70, 130, 180));
        addInfoRow(infoPanel, "Views:", String.valueOf(song.getViews()), new Color(70, 130, 180));

        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(15));

        // Lyrics panel
        JPanel lyricsPanel = createStyledPanel("Lyrics", primaryColor);
        JLabel lyricsLabel = new JLabel("<html><div style='padding:5px;'>" +
                song.getLyrics().replace("\n", "<br>") + "</div></html>");
        lyricsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane lyricsScroll = new JScrollPane(lyricsLabel);
        lyricsScroll.setOpaque(false);
        lyricsScroll.getViewport().setOpaque(false);
        lyricsScroll.setBorder(null);
        lyricsPanel.add(lyricsScroll);

        centerPanel.add(lyricsPanel);
        centerPanel.add(Box.createVerticalStrut(15));

        // Comments panel
        JPanel commentsPanel = createStyledPanel("Comments (" + song.getComments().size() + ")", primaryColor);
        JPanel commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        commentsListPanel.setOpaque(false);

        for (Comment comment : song.getComments()) {
            Account commenter = accounts.values().stream()
                    .filter(a -> a.getId() == comment.getUserId())
                    .findFirst()
                    .orElse(null);
            String username = commenter != null ? commenter.getUsername() : "Unknown";

            JPanel commentPanel = new JPanel(new BorderLayout());
            commentPanel.setOpaque(false);
            commentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            JLabel userLabel = new JLabel(username + ":");
            userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            userLabel.setForeground(primaryColor);
            commentPanel.add(userLabel, BorderLayout.NORTH);

            JLabel commentLabel = new JLabel("<html><div style='padding:3px;'>" +
                    comment.getText().replace("\n", "<br>") + "</div></html>");
            commentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            commentPanel.add(commentLabel, BorderLayout.CENTER);

            commentsListPanel.add(commentPanel);
        }

        JScrollPane commentsScroll = new JScrollPane(commentsListPanel);
        commentsScroll.setOpaque(false);
        commentsScroll.getViewport().setOpaque(false);
        commentsScroll.setBorder(null);
        commentsPanel.add(commentsScroll);

        centerPanel.add(commentsPanel);

        // Add center panel to main panel with scroll
        JScrollPane centerScroll = new JScrollPane(centerPanel);
        centerScroll.setOpaque(false);
        centerScroll.getViewport().setOpaque(false);
        centerScroll.setBorder(null);
        mainPanel.add(centerScroll, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        if (currentUser instanceof User) {
            // Like button
            JButton likeButton = createStyledButton(
                    song.isLikedBy(currentUser.getId()) ? "♥ Unlike" : "♥ Like",
                    song.isLikedBy(currentUser.getId()) ? accentColor : primaryColor);
            likeButton.addActionListener(e -> {
                User user = (User) currentUser;
                boolean wasLiked = song.isLikedBy(user.getId());

                if (wasLiked) {
                    song.unlike(user.getId());
                    user.likeSong(song.getId());
                    likeButton.setText("♥ Like");
                    likeButton.setForeground(primaryColor);
                } else {
                    song.like(user.getId());
                    user.likeSong(song.getId());
                    likeButton.setText("♥ Unlike");
                    likeButton.setForeground(accentColor);
                }
                // Update likes display using the stored reference
                likesLabel.setText(String.valueOf(song.getLikes()));

                // Force UI refresh
                infoPanel.revalidate();
                infoPanel.repaint();
            });
            buttonPanel.add(likeButton);

            // Comment button
            JButton commentButton = createStyledButton("💬 Add Comment", primaryColor);
            commentButton.addActionListener(e -> {
                JPanel inputPanel = new JPanel(new BorderLayout());
                JTextArea commentArea = new JTextArea(3, 20);
                commentArea.setLineWrap(true);
                commentArea.setWrapStyleWord(true);
                commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JScrollPane scrollPane = new JScrollPane(commentArea);
                inputPanel.add(new JLabel("Your comment:"), BorderLayout.NORTH);
                inputPanel.add(scrollPane, BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(detailsFrame, inputPanel,
                        "Add Comment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION && !commentArea.getText().trim().isEmpty()) {
                    song.addComment(new Comment(song.getComments().size() + 1,
                            currentUser.getId(), song.getId(), commentArea.getText()));
                    // Refresh the view
                    detailsFrame.dispose();
                    showSongDetails(song);
                }
            });
            buttonPanel.add(commentButton);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailsFrame.add(mainPanel);
        detailsFrame.setVisible(true);
    }

    // Helper methods for styling
    private static JPanel createStyledPanel(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(color, 1),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12),
                        color),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return panel;
    }

    private static JLabel addInfoRow(JPanel panel, String labelText, String value, Color color) {
        // Create a panel for the label-value pair
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rowPanel.setOpaque(false); // Make it transparent

        // Create and style the label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(color);

        // Create and style the value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setName(labelText.replace(":", "").trim().toLowerCase() + "-value"); // For easy reference

        // Add components to the row panel
        rowPanel.add(label);
        rowPanel.add(valueLabel);

        // Add the row panel to the main panel
        panel.add(rowPanel);

        // Return the value label for potential updates
        return valueLabel;
    }

    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void showAdminModerationPanel() {
        if (!(currentUser instanceof Admin)) {
            JOptionPane.showMessageDialog(null, "Admin privileges required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Admin Moderation");
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Account Management Tab
        JPanel accountPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> accountModel = new DefaultListModel<>();
        JList<String> accountList = new JList<>(accountModel);

        // Populate with all accounts
        accounts.values().forEach(acc -> {
            String status = acc.isBanned() ? "Banned" :
                    acc.isActive() ? "Active" : "Inactive";
            accountModel.addElement(String.format("%d: %s (%s) - %s",
                    acc.getId(), acc.getUsername(), acc.getEmail(), status));
        });

        JPanel accountButtonPanel = new JPanel();
        JButton banButton = new JButton("Ban");
        JButton restoreButton = new JButton("Restore");

        banButton.addActionListener(e -> {
            int index = accountList.getSelectedIndex();
            if (index >= 0) {
                Account acc = new ArrayList<>(accounts.values()).get(index);
                String reason = JOptionPane.showInputDialog(frame, "Enter ban reason:");
                if (reason != null && !reason.trim().isEmpty()) {
                    boolean success = ((Admin)currentUser).banAccount(acc.getId(), reason);
                    if (success) {
                        accountModel.set(index, accountModel.get(index).replace("Active", "Banned"));
                    }
                }
            }
        });

        restoreButton.addActionListener(e -> {
            int index = accountList.getSelectedIndex();
            if (index >= 0) {
                Account acc = new ArrayList<>(accounts.values()).get(index);
                if (!acc.isActive()) {
                    String reason = JOptionPane.showInputDialog(frame, "Enter restoration reason:");
                    if (reason != null && !reason.trim().isEmpty()) {
                        boolean success = ((Admin)currentUser).restoreAccount(acc.getId(), reason);
                        if (success) {
                            accountModel.set(index, accountModel.get(index).replace("Banned", "Active"));
                        }
                    }
                }
            }
        });

        accountButtonPanel.add(banButton);
        accountButtonPanel.add(restoreButton);
        accountPanel.add(new JScrollPane(accountList), BorderLayout.CENTER);
        accountPanel.add(accountButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Accounts", accountPanel);

        // 2. Content Moderation Tab
        JPanel contentPanel = new JPanel(new BorderLayout());
        JTabbedPane contentTabs = new JTabbedPane();

        // Songs Sub-tab
        DefaultListModel<String> songsModel = new DefaultListModel<>();
        JList<String> songsList = new JList<>(songsModel);
        songs.forEach(song -> songsModel.addElement(
                String.format("%d: %s by %s", song.getId(), song.getTitle(), getArtistNames(song.getArtistIds()))));

        JButton deleteSongButton = new JButton("Delete");
        deleteSongButton.addActionListener(e -> {
            int index = songsList.getSelectedIndex();
            if (index >= 0) {
                Song song = new ArrayList<>(songs).get(index);
                String reason = JOptionPane.showInputDialog(frame, "Enter deletion reason:");
                if (reason != null && !reason.trim().isEmpty()) {
                    boolean success = ((Admin)currentUser).deleteSong(song.getId(), reason);
                    if (success) {
                        songsModel.remove(index);
                    }
                }
            }
        });

        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.add(new JScrollPane(songsList), BorderLayout.CENTER);
        songsPanel.add(deleteSongButton, BorderLayout.SOUTH);
        contentTabs.addTab("Songs", songsPanel);

        // Add similar tabs for Albums and Comments as needed...

        contentPanel.add(contentTabs, BorderLayout.CENTER);
        tabbedPane.addTab("Content", contentPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createAccountManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);

        // Populate account list
        accounts.values().forEach(acc -> {
            String status = acc.isActive() ? "Active" : "Banned";
            String type = acc instanceof Artist ? "Artist" : "User";
            model.addElement(String.format("%d: %s (%s) - %s - %s",
                    acc.getId(), acc.getUsername(), acc.getEmail(), type, status));
        });

        JPanel buttonPanel = new JPanel();
        JButton banBtn = new JButton("Ban");
        JButton restoreBtn = new JButton("Restore");

        banBtn.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                Account acc = accounts.values().stream()
                        .skip(index)
                        .findFirst()
                        .orElse(null);

                if (acc != null) {
                    String reason = JOptionPane.showInputDialog("Ban reason:");
                    if (reason != null && !reason.isEmpty()) {
                        boolean success = ((Admin)currentUser).banAccount(acc.getId(), reason);
                        if (success) {
                            model.set(index, model.get(index).replace("Active", "Banned"));
                        }
                    }
                }
            }
        });

        restoreBtn.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                Account acc = accounts.values().stream()
                        .skip(index)
                        .findFirst()
                        .orElse(null);

                if (acc != null && !acc.isActive()) {
                    String reason = JOptionPane.showInputDialog("Restoration reason:");
                    if (reason != null && !reason.isEmpty()) {
                        boolean success = ((Admin)currentUser).restoreAccount(acc.getId(), reason);
                        if (success) {
                            model.set(index, model.get(index).replace("Banned", "Active"));
                        }
                    }
                }
            }
        });

        buttonPanel.add(banBtn);
        buttonPanel.add(restoreBtn);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel createContentModerationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // Songs tab
        DefaultListModel<String> songsModel = new DefaultListModel<>();
        JList<String> songsList = new JList<>(songsModel);
        songs.forEach(s -> songsModel.addElement(
                String.format("%d: %s - %s", s.getId(), s.getTitle(), getArtistNames(s.getArtistIds())))
        );

        JButton deleteSongBtn = new JButton("Delete");
        deleteSongBtn.addActionListener(e -> {
            int index = songsList.getSelectedIndex();
            if (index >= 0) {
                Song song = songs.stream().skip(index).findFirst().orElse(null);
                if (song != null) {
                    String reason = JOptionPane.showInputDialog("Deletion reason:");
                    if (reason != null && !reason.isEmpty()) {
                        boolean success = ((Admin)currentUser).deleteSong(song.getId(), reason);
                        if (success) {
                            songsModel.remove(index);
                        }
                    }
                }
            }
        });

        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.add(new JScrollPane(songsList), BorderLayout.CENTER);
        songsPanel.add(deleteSongBtn, BorderLayout.SOUTH);
        tabs.addTab("Songs", songsPanel);

        // Similar tabs for Albums and Comments...

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private static boolean checkAccess() {
        if (currentUser == null || currentUser.isBanned() || !currentUser.isActive()) {
            JOptionPane.showMessageDialog(null,
                    "Access denied. Your account may be banned or inactive.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}