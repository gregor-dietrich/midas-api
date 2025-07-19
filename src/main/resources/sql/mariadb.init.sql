SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

--
-- Structure for table `pages`
--

CREATE TABLE `pages` (
  `id` bigint UNSIGNED NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Relations for table `pages`:
--

-- --------------------------------------------------------

--
-- Structure for table `posts`
--

CREATE TABLE `posts` (
  `id` bigint UNSIGNED NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint UNSIGNED DEFAULT NULL,
  `category_id` bigint UNSIGNED DEFAULT NULL,
  `published` tinyint(1) NOT NULL DEFAULT 0,
  `commentable` tinyint(1) NOT NULL DEFAULT 0,
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  `last_edit` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `posts`:
--   `user_id`
--       `users` -> `id`
--   `category_id`
--       `post_categories` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `post_categories`
--

CREATE TABLE `post_categories` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `post_categories`:
--   `parent_id`
--       `post_categories` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `post_comments`
--

CREATE TABLE `post_comments` (
  `id` bigint UNSIGNED NOT NULL,
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `post_id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED,
  `created` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `post_comments`:
--   `user_id`
--       `users` -> `id`
--   `post_id`
--       `posts` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint UNSIGNED NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `salt` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rank_id` bigint UNSIGNED NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `banned` tinyint(1) NOT NULL DEFAULT 0,
  `activated` tinyint(1) NOT NULL DEFAULT 0,
  `activation_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  `last_login` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `users`:
--   `rank_id`
--       `user_ranks` -> `id`
--

--
-- Inserts for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `rank_id`, `activated`, `banned`, `activation_key`) VALUES
(1, 'admin', '3HWqMv8tiSEbBcsUfxqBx7kY4vw+cSvG7OQXp9uzM0w=', '0l/SGC6gqKwYWjw7sm2IrwzIcAjq/QkO9xXcG/LC56c=', 3, 1, 0, NULL),
(2, 'moderator', '4IV82pA2Q1BmNaIS+de+1yqS1UQSwSE21xU6kkSda6c=', '52Mdt2qexvXu99Gm+wB0iv8V3n5leK5XR1zGLNBeDQg=', 2, 1, 0, NULL),
(3, 'user', 'KD+VrVb86w2Z7Ei8UjsHbbZ/awEnReWeC8t0656EqMM=', 'e3/FXXV1XuCaSHXxtwR1K0Lc5N/Al7rTJ6Kkpx1S2Uk=', 1, 1, 0, NULL),
(4, 'bannedUser', 'tE04ap7GWryC2vCAzJpcFilsSGPozJsrAjRyZaXqeXM=', 'QLxT3YTXJuEFeaG71Shryr5BUkW+i4vocjmpKXT8h4k=', 1, 1, 1, NULL),
(5, 'notActivatedUser', 'xA28JSHhTwUxoBfOS0RCTPfnMzkvzB5i724zzo25iYI=', 'BVCeBwQYMx/7sZ8tIiR9wCvQ/plNQzTwG0neblWWu3k=', 1, 0, 0, '21b8efb5-8eb0-4530-9d9a-cdbc33ba7164');

-- --------------------------------------------------------

--
-- Structure for table `user_accounts`
--

CREATE TABLE `user_accounts` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Relations for table `user_accounts`:
--

-- --------------------------------------------------------

--
-- Structure for table `user_accounts_meta`
--

CREATE TABLE `user_accounts_meta` (
  `id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `account_id` bigint UNSIGNED NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Relations for table `user_accounts_meta`:
--   `user_id`
--       `users` -> `id`
--   `account_id`
--       `user_accounts` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `user_groups`
--

CREATE TABLE `user_groups` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Relations for table `user_groups`:
--

-- --------------------------------------------------------

--
-- Structure for table `user_groups_meta`
--

CREATE TABLE `user_groups_meta` (
  `id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `group_id` bigint UNSIGNED NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Relations for table `user_groups_meta`:
--   `user_id`
--       `users` -> `id`
--   `group_id`
--       `user_groups` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `user_payments`
--

CREATE TABLE `user_payments` (
  `id` bigint UNSIGNED NOT NULL,
  `target_id` bigint UNSIGNED DEFAULT NULL,
  `source_id` bigint UNSIGNED DEFAULT NULL,
  `user_id` bigint UNSIGNED,
  `comment` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date` date NOT NULL DEFAULT curdate(),
  `amount` decimal(15,2) NOT NULL DEFAULT 0.00,
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  `last_edit` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `user_payments`:
--   `user_id`
--       `users` -> `id`
--   `source_id`
--       `user_accounts` -> `id`
--   `target_id`
--       `user_accounts` -> `id`
--

-- --------------------------------------------------------

--
-- Structure for table `user_ranks`
--

CREATE TABLE `user_ranks` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `post_add` tinyint(1) NOT NULL DEFAULT 0,
  `post_delete` tinyint(1) NOT NULL DEFAULT 0,
  `post_edit` tinyint(1) NOT NULL DEFAULT 0,
  `post_category_add` tinyint(1) NOT NULL DEFAULT 0,
  `post_category_delete` tinyint(1) NOT NULL DEFAULT 0,
  `post_category_edit` tinyint(1) NOT NULL DEFAULT 0,
  `post_comment_add` tinyint(1) NOT NULL DEFAULT 0,
  `post_comment_delete` tinyint(1) NOT NULL DEFAULT 0,
  `post_comment_edit` tinyint(1) NOT NULL DEFAULT 0,
  `user_add` tinyint(1) NOT NULL DEFAULT 0,
  `user_delete` tinyint(1) NOT NULL DEFAULT 0,
  `user_edit` tinyint(1) NOT NULL DEFAULT 0,
  `user_group_add` tinyint(1) NOT NULL DEFAULT 0,
  `user_group_delete` tinyint(1) NOT NULL DEFAULT 0,
  `user_group_edit` tinyint(1) NOT NULL DEFAULT 0,
  `user_rank_add` tinyint(1) NOT NULL DEFAULT 0,
  `user_rank_delete` tinyint(1) NOT NULL DEFAULT 0,
  `user_rank_edit` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Relations for table `user_ranks`:
--

--
-- Inserts for table `user_ranks`
--

INSERT INTO `user_ranks` (`id`, `name`, `post_add`, `post_delete`, `post_edit`, `post_category_add`, `post_category_delete`, `post_category_edit`, `post_comment_add`, `post_comment_delete`, `post_comment_edit`, `user_add`, `user_delete`, `user_edit`, `user_group_add`, `user_group_delete`, `user_group_edit`, `user_rank_add`, `user_rank_delete`, `user_rank_edit`) VALUES
(1, 'User', 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(2, 'Moderator', 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(3, 'Administrator', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

--
-- Indexes for exported tables
--

--
-- Indexes for table `pages`
--
ALTER TABLE `pages`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `pages` ADD FULLTEXT KEY `content` (`content`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `posts_ibfk_1` (`user_id`),
  ADD KEY `posts_ibfk_2` (`category_id`);
ALTER TABLE `posts` ADD FULLTEXT KEY `content` (`content`);

--
-- Indexes for table `post_categories`
--
ALTER TABLE `post_categories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `post_categories_ibfk_1` (`parent_id`);

--
-- Indexes for table `post_comments`
--
ALTER TABLE `post_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `post_comments_ibfk_1` (`user_id`),
  ADD KEY `post_comments_ibfk_2` (`post_id`);
ALTER TABLE `post_comments` ADD FULLTEXT KEY `content` (`content`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_username` (`username`),
  ADD UNIQUE KEY `users_email` (`email`),
  ADD KEY `users_ibfk_1` (`rank_id`);

--
-- Indexes for table `user_accounts`
--
ALTER TABLE `user_accounts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_accounts_meta`
--
ALTER TABLE `user_accounts_meta`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`account_id`),
  ADD KEY `account_id` (`account_id`);

--
-- Indexes for table `user_groups`
--
ALTER TABLE `user_groups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_groups_meta`
--
ALTER TABLE `user_groups_meta`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`group_id`),
  ADD KEY `group_id` (`group_id`);

--
-- Indexes for table `user_payments`
--
ALTER TABLE `user_payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `payments_ibfk_1` (`user_id`),
  ADD KEY `payments_ibfk_2` (`source_id`),
  ADD KEY `payments_ibfk_3` (`target_id`);

--
-- Indexes for table `user_ranks`
--
ALTER TABLE `user_ranks`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_ranks_name` (`name`);

--
-- AUTO_INCREMENT for exported tables
--

--
-- AUTO_INCREMENT for table `pages`
--
ALTER TABLE `pages`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `post_categories`
--
ALTER TABLE `post_categories`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `post_comments`
--
ALTER TABLE `post_comments`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_accounts`
--
ALTER TABLE `user_accounts`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_accounts_meta`
--
ALTER TABLE `user_accounts_meta`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_groups`
--
ALTER TABLE `user_groups`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_groups_meta`
--
ALTER TABLE `user_groups_meta`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_payments`
--
ALTER TABLE `user_payments`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_ranks`
--
ALTER TABLE `user_ranks`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for exported tables
--

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `post_categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `post_categories`
--
ALTER TABLE `post_categories`
  ADD CONSTRAINT `post_categories_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `post_categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `post_comments`
--
ALTER TABLE `post_comments`
  ADD CONSTRAINT `post_comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `post_comments_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`rank_id`) REFERENCES `user_ranks` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user_accounts_meta`
--
ALTER TABLE `user_accounts_meta`
  ADD CONSTRAINT `user_accounts_meta_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_accounts_meta_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `user_accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_groups_meta`
--
ALTER TABLE `user_groups_meta`
  ADD CONSTRAINT `user_groups_meta_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_groups_meta_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_payments`
--
ALTER TABLE `user_payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`source_id`) REFERENCES `user_accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `payments_ibfk_3` FOREIGN KEY (`target_id`) REFERENCES `user_accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
