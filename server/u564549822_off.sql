
-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 23, 2014 at 03:50 PM
-- Server version: 5.1.66
-- PHP Version: 5.2.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `u564549822_off`
--

-- --------------------------------------------------------

--
-- Table structure for table `states`
--

CREATE TABLE IF NOT EXISTS `states` (
  `user` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `status` int(1) NOT NULL,
  `since` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `dev_tok` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `states`
--

INSERT INTO `states` (`user`, `status`, `since`, `dev_tok`) VALUES
('samer', 0, '2014-10-19 15:22:30', 'APA91bHvzHg4r4VYdHXdAUMC7v7Ik1N_sEfaT7mFo3AbQehExPYrKKo7jMR5RkPLR0-1Gpt64DtfJAfGqJNgWVjEZ0KEABcJcVGPjZ7wFWHk2EuR7ZI5veLMGp26_XwYhxgFw45nzkUZaTA-0cJ5DvLmyFUt3SPt4ERUAoAY-AE9dLbpk_8QBVQ'),
('omar', 0, '2014-10-23 19:05:36', 'APA91bE7pMJLdeVMtD6JoO0w2oFNcdtMXrlx7qozTuO18bvvIlgs1rFz9TeAqvFlXTk_-Q7hpwas7hNfYs0R4_z2G_AnsWNMaMJtFPp855hwXtjxgk-bbJZhGtwgicn_XC2eWcYtwfUiWo_kOsk-mH_nWumJqu0HdurBLgCl4d3rw36hbuXfnOU'),
('zaid', 1, '2014-10-23 07:33:57', 'APA91bGZjGfT1wVzEMOsZL0L9tQXbi08xP4gWDui23DtwX6IKTHm6szeF8X5kaIMRKQwLtI6_A9NGy-Sw3pVs2jVKzo3FxCWk59ta9Ynw7qK-NbC3-ATZrmYoqpWTrbss6wzzd5wHekI3Q3pFicMACX07YLx54CGMMp2CgAseMDTuWels0s8h3w');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
