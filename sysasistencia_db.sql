-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 24, 2025 at 12:41 PM
-- Server version: 8.0.30
-- PHP Version: 8.2.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sysasistencia_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `upeu_accesos`
--

CREATE TABLE `upeu_accesos` (
  `id_acceso` bigint NOT NULL,
  `icono` varchar(60) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `url` varchar(100) NOT NULL,
  `id_acceso_padre` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_accesos`
--

INSERT INTO `upeu_accesos` (`id_acceso`, `icono`, `nombre`, `url`, `id_acceso_padre`) VALUES
(1, 'fa-users', 'Usuarios', '/usuarios', NULL),
(2, 'fa-user-shield', 'Roles', '/roles', NULL),
(3, 'fa-cog', 'Configuración', '/configuracion', NULL),
(4, 'fa-clipboard-list', 'Matrículas', '/matriculas', NULL),
(5, 'fa-file-excel', 'Importar Excel', '/matriculas/importar', NULL),
(6, 'fa-building', 'Sedes', '/sedes', NULL),
(7, 'fa-university', 'Facultades', '/facultades', NULL),
(8, 'fa-graduation-cap', 'Programas', '/programas', NULL),
(9, 'fa-chart-bar', 'Reportes', '/reportes', NULL),
(10, 'fa-tachometer-alt', 'Dashboard Admin', '/dashboard/admin', NULL),
(11, 'fa-chart-line', 'Dashboard Líder', '/dashboard/lider', NULL),
(12, 'fa-chart-pie', 'Dashboard Integrante', '/dashboard/integrante', NULL),
(13, 'fa-calendar-alt', 'Eventos Generales', '/eventos-generales', NULL),
(14, 'fa-clock', 'Sesiones', '/eventos-especificos', NULL),
(15, 'fa-users', 'Grupos Generales', '/grupos-generales', NULL),
(16, 'fa-user-friends', 'Grupos Pequeños', '/grupos-pequenos', NULL),
(18, 'fa-user-tie', 'Mis Grupos', '/grupos-pequenos/lider', NULL),
(19, 'fa-check-square', 'Registrar Asistencia', '/asistencias/registrar', NULL),
(20, 'fa-list-alt', 'Ver Asistencias', '/asistencias', NULL),
(21, 'fa-qrcode', 'Escanear QR', '/asistencias/escanear', NULL),
(22, 'fa-user-check', 'Mis Asistencias', '/asistencias/persona', NULL),
(23, 'fa-chart-line', 'Reportes Eventos', '/asistencias/reporte', NULL),
(24, 'fa-calendar-day', 'Periodos', '/periodos', NULL),
(25, 'fa-user-edit', 'Editar Perfil', '/personas/my-profile', NULL),
(26, 'fa-users', 'Gestión de Usuarios', '/users', NULL),
(29, 'fa-user-plus', 'Gestión de Participantes', '/participantes', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_acceso_rol`
--

CREATE TABLE `upeu_acceso_rol` (
  `rol_id` bigint NOT NULL,
  `acceso_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_acceso_rol`
--

INSERT INTO `upeu_acceso_rol` (`rol_id`, `acceso_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(1, 22),
(1, 23),
(1, 24),
(1, 25),
(1, 26),
(1, 29),
(2, 4),
(2, 5),
(2, 6),
(2, 7),
(2, 8),
(2, 9),
(2, 10),
(2, 13),
(2, 14),
(2, 15),
(2, 16),
(2, 20),
(2, 23),
(2, 24),
(2, 25),
(2, 26),
(2, 29),
(3, 11),
(3, 18),
(3, 19),
(3, 20),
(3, 22),
(3, 25),
(3, 29),
(4, 12),
(4, 21),
(4, 22),
(4, 25);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_asistencia`
--

CREATE TABLE `upeu_asistencia` (
  `id_asistencia` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estado` enum('AUSENTE','JUSTIFICADO','PRESENTE','TARDE') NOT NULL,
  `fecha_hora_registro` datetime(6) NOT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(11,8) DEFAULT NULL,
  `observacion` text,
  `evento_especifico_id` bigint NOT NULL,
  `persona_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_asistencia`
--

INSERT INTO `upeu_asistencia` (`id_asistencia`, `created_at`, `estado`, `fecha_hora_registro`, `latitud`, `longitud`, `observacion`, `evento_especifico_id`, `persona_id`) VALUES
(1, '2025-11-11 01:12:09.995606', 'PRESENTE', '2025-11-11 01:12:09.991607', NULL, NULL, 'Registrado por QR', 4, 2),
(2, '2025-11-11 02:01:40.991755', 'PRESENTE', '2025-11-11 02:01:40.991755', NULL, NULL, 'Marcado por líder como: PRESENTE (Registrado por líder)', 4, 3),
(3, '2025-11-11 02:03:45.991175', 'TARDE', '2025-11-11 02:03:45.990177', NULL, NULL, 'Registrado por QR', 4, 4),
(4, '2025-11-11 08:01:46.780951', 'PRESENTE', '2025-11-11 08:01:46.780951', NULL, NULL, 'Registrado por QR', 5, 2),
(5, '2025-11-11 08:05:58.572722', 'PRESENTE', '2025-11-11 08:05:58.572722', NULL, NULL, 'Registrado por QR', 5, 3),
(6, '2025-11-11 11:32:34.160562', 'PRESENTE', '2025-11-11 11:32:34.146293', NULL, NULL, 'Marcado por líder como: PRESENTE (Actualizado por líder)', 6, 2),
(7, '2025-11-16 17:04:22.744113', 'TARDE', '2025-11-16 17:04:22.725406', NULL, NULL, 'Registro por QR desde app móvil', 7, 2),
(8, '2025-11-16 18:38:54.073651', 'PRESENTE', '2025-11-16 18:38:54.073651', NULL, NULL, 'Registro por QR desde app móvil', 8, 2),
(9, '2025-11-17 15:55:24.068099', 'PRESENTE', '2025-11-17 15:55:24.064002', NULL, NULL, 'Registro por QR desde app móvil', 9, 2),
(10, '2025-11-18 05:59:20.009737', 'PRESENTE', '2025-11-18 05:59:20.008559', NULL, NULL, 'Registro por QR desde app móvil', 10, 2),
(11, '2025-11-23 19:35:10.008396', 'PRESENTE', '2025-11-23 19:35:10.008396', NULL, NULL, 'Registro por QR desde app móvil', 11, 3),
(12, '2025-11-23 21:10:45.139073', 'TARDE', '2025-11-23 21:10:45.129949', NULL, NULL, 'Registro por QR desde app móvil', 11, 2),
(13, '2025-11-23 22:16:10.163638', 'PRESENTE', '2025-11-23 22:16:10.163638', NULL, NULL, 'Asistencia automática por generar QR', 12, 15),
(14, '2025-11-23 22:40:38.307715', 'TARDE', '2025-11-23 22:40:38.307715', NULL, NULL, 'Registro por QR desde app móvil', 12, 3),
(15, '2025-11-23 22:41:05.721064', 'PRESENTE', '2025-11-23 22:41:05.721064', NULL, NULL, 'Marcado por líder como: PRESENTE (Registrado por líder)', 12, 2),
(16, '2025-11-23 22:41:08.156734', 'PRESENTE', '2025-11-23 22:41:08.156734', NULL, NULL, 'Marcado por líder como: PRESENTE (Registrado por líder)', 12, 4);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_evento_especifico`
--

CREATE TABLE `upeu_evento_especifico` (
  `id_evento_especifico` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text,
  `estado` enum('CANCELADO','EN_CURSO','FINALIZADO','PROGRAMADO') DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora_fin` time(6) NOT NULL,
  `hora_inicio` time(6) NOT NULL,
  `lugar` varchar(200) DEFAULT NULL,
  `nombre_sesion` varchar(200) NOT NULL,
  `tolerancia_minutos` int DEFAULT NULL,
  `evento_general_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_evento_especifico`
--

INSERT INTO `upeu_evento_especifico` (`id_evento_especifico`, `created_at`, `descripcion`, `estado`, `fecha`, `hora_fin`, `hora_inicio`, `lugar`, `nombre_sesion`, `tolerancia_minutos`, `evento_general_id`) VALUES
(2, '2025-11-11 00:14:13.926134', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-22', '10:00:00.000000', '08:00:00.000000', 'Auditorio', 'fem (SATURDAY)', 5, 1),
(3, '2025-11-11 00:14:13.929661', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-29', '10:00:00.000000', '08:00:00.000000', 'Auditorio', 'fem (SATURDAY)', 5, 1),
(4, '2025-11-11 00:15:11.286119', 'A SDAS DA DASD AD S ', 'PROGRAMADO', '2025-11-11', '06:00:00.000000', '01:10:00.000000', 'Auditorio', 'femAS', 5, 1),
(5, '2025-11-11 07:58:13.109335', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-11', '09:00:00.000000', '08:00:00.000000', 'Auditorio sThal', 'fem', 10, 1),
(6, '2025-11-11 11:29:27.065916', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-11', '12:30:00.000000', '11:30:00.000000', 'Auditorio sThal', 'fem', 5, 1),
(7, '2025-11-16 16:11:04.134968', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-16', '17:10:00.000000', '16:10:00.000000', 'Auditorio sThal', 'fem', 20, 1),
(8, '2025-11-16 18:27:53.046177', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-16', '20:00:00.000000', '18:28:00.000000', 'Auditorio sThal', 'fem', 20, 1),
(9, '2025-11-17 15:40:12.849973', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-17', '20:00:00.000000', '16:00:00.000000', 'Auditorio sThal', 'fem', 20, 1),
(10, '2025-11-18 05:57:11.105926', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-18', '08:00:00.000000', '05:57:00.000000', 'Auditorio sThal', 'fem', 20, 1),
(11, '2025-11-23 19:34:06.392281', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-23', '22:00:00.000000', '19:30:00.000000', 'Auditorio sThal', 'ferm prueba hoy', 30, 1),
(12, '2025-11-23 22:15:50.733700', 'asdddddd SD S asASDsaD', 'PROGRAMADO', '2025-11-23', '23:00:00.000000', '22:00:00.000000', 'Auditorio sThal', 'fem prueba asistencia lider', 30, 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_evento_general`
--

CREATE TABLE `upeu_evento_general` (
  `id_evento_general` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text,
  `estado` enum('ACTIVO','CANCELADO','FINALIZADO') DEFAULT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_inicio` date NOT NULL,
  `lugar` varchar(200) DEFAULT NULL,
  `nombre` varchar(200) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `periodo_id` bigint NOT NULL,
  `programa_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_evento_general`
--

INSERT INTO `upeu_evento_general` (`id_evento_general`, `created_at`, `descripcion`, `estado`, `fecha_fin`, `fecha_inicio`, `lugar`, `nombre`, `updated_at`, `periodo_id`, `programa_id`) VALUES
(1, '2025-11-11 00:12:55.780642', 'asdddddd SD S asASDsaD', 'ACTIVO', '2025-11-29', '2025-11-15', 'Auditorio sThal', 'FEM', '2025-11-11 07:57:24.260272', 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_facultad`
--

CREATE TABLE `upeu_facultad` (
  `id_facultad` bigint NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `sede_id` bigint NOT NULL,
  `id_sede` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_facultad`
--

INSERT INTO `upeu_facultad` (`id_facultad`, `descripcion`, `nombre`, `sede_id`, `id_sede`) VALUES
(1, 'Facultad de Ingeniería y Arquitectura', 'Facultad de Ingeniería y Arquitectura', 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_grupo_general`
--

CREATE TABLE `upeu_grupo_general` (
  `id_grupo_general` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text,
  `nombre` varchar(100) NOT NULL,
  `evento_general_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_grupo_general`
--

INSERT INTO `upeu_grupo_general` (`id_grupo_general`, `created_at`, `descripcion`, `nombre`, `evento_general_id`) VALUES
(1, '2025-11-11 00:15:51.300039', 'QWE DA DAFDS ASF SQWEASD c  v', 'Betania', 1),
(2, '2025-11-23 14:21:21.751938', 'axadvz v vasdv asda', 'Samuel', 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_grupo_participante`
--

CREATE TABLE `upeu_grupo_participante` (
  `id_grupo_participante` bigint NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT NULL,
  `fecha_inscripcion` datetime(6) DEFAULT NULL,
  `grupo_pequeno_id` bigint NOT NULL,
  `persona_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_grupo_participante`
--

INSERT INTO `upeu_grupo_participante` (`id_grupo_participante`, `estado`, `fecha_inscripcion`, `grupo_pequeno_id`, `persona_id`) VALUES
(1, 'ACTIVO', '2025-11-11 00:17:03.501495', 1, 2),
(2, 'ACTIVO', '2025-11-11 00:17:04.676349', 1, 3),
(3, 'ACTIVO', '2025-11-11 00:17:05.835041', 1, 4),
(4, 'ACTIVO', '2025-11-24 01:24:25.862129', 1, 5),
(5, 'INACTIVO', '2025-11-11 08:06:29.969352', 1, 7),
(6, 'ACTIVO', '2025-11-24 01:25:22.364146', 1, 6),
(7, 'ACTIVO', '2025-11-24 00:06:51.913185', 1, 8),
(8, 'ACTIVO', '2025-11-24 00:06:53.030816', 1, 9),
(9, 'ACTIVO', '2025-11-24 00:06:53.881550', 1, 10),
(18, 'ACTIVO', '2025-11-24 00:06:58.705179', 1, 14),
(21, 'ACTIVO', '2025-11-24 00:06:57.613666', 1, 13),
(55, 'ACTIVO', '2025-11-24 00:06:56.281504', 1, 12),
(56, 'INACTIVO', '2025-11-23 23:56:43.720182', 1, 11);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_grupo_pequeno`
--

CREATE TABLE `upeu_grupo_pequeno` (
  `id_grupo_pequeno` bigint NOT NULL,
  `capacidad_maxima` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text,
  `nombre` varchar(100) NOT NULL,
  `grupo_general_id` bigint NOT NULL,
  `lider_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_grupo_pequeno`
--

INSERT INTO `upeu_grupo_pequeno` (`id_grupo_pequeno`, `capacidad_maxima`, `created_at`, `descripcion`, `nombre`, `grupo_general_id`, `lider_id`) VALUES
(1, 15, '2025-11-11 00:16:46.543957', 'JF GHJDFJSASH', 'GRUPO A', 1, 15);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_matricula`
--

CREATE TABLE `upeu_matricula` (
  `id_matricula` bigint NOT NULL,
  `ciclo` varchar(10) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `fecha_matricula` datetime(6) DEFAULT NULL,
  `grupo` varchar(10) DEFAULT NULL,
  `modalidad_estudio` varchar(50) DEFAULT NULL,
  `modo_contrato` varchar(50) DEFAULT NULL,
  `facultad_id` bigint NOT NULL,
  `periodo_id` bigint NOT NULL,
  `persona_id` bigint NOT NULL,
  `programa_id` bigint NOT NULL,
  `sede_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_matricula`
--

INSERT INTO `upeu_matricula` (`id_matricula`, `ciclo`, `estado`, `fecha_matricula`, `grupo`, `modalidad_estudio`, `modo_contrato`, `facultad_id`, `periodo_id`, `persona_id`, `programa_id`, `sede_id`) VALUES
(1, '1', 'ACTIVO', '2025-11-16 18:06:42.296297', '1', 'Presencial', 'Regular', 1, 2, 2, 1, 1),
(2, '1', 'ACTIVO', '2025-11-16 18:06:42.326564', '1', 'Presencial', 'Regular', 1, 2, 3, 1, 1),
(3, '1', 'ACTIVO', '2025-11-16 18:06:42.392555', '1', 'Presencial', 'Regular', 1, 2, 4, 1, 1),
(4, '1', 'ACTIVO', '2025-11-16 18:06:42.411276', '1', 'Presencial', 'Regular', 1, 2, 5, 1, 1),
(5, '1', 'ACTIVO', '2025-11-16 18:06:42.433722', '1', 'Presencial', 'Regular', 1, 2, 6, 1, 1),
(6, '1', 'ACTIVO', '2025-11-16 18:06:42.451845', '1', 'Presencial', 'Regular', 1, 2, 7, 1, 1),
(7, '1', 'ACTIVO', '2025-11-16 18:06:42.468166', '1', 'Presencial', 'Regular', 1, 2, 8, 1, 1),
(8, '1', 'ACTIVO', '2025-11-16 18:06:42.499464', '1', 'Presencial', 'Regular', 1, 2, 9, 1, 1),
(9, '1', 'ACTIVO', '2025-11-16 18:06:42.518017', '1', 'Presencial', 'Regular', 1, 2, 10, 1, 1),
(10, '1', 'ACTIVO', '2025-11-16 18:06:42.535659', '1', 'Presencial', 'Regular', 1, 2, 11, 1, 1),
(11, '1', 'ACTIVO', '2025-11-16 18:06:42.565427', '1', 'Presencial', 'Regular', 1, 2, 12, 1, 1),
(12, '1', 'ACTIVO', '2025-11-16 18:06:42.582906', '1', 'Presencial', 'Regular', 1, 2, 13, 1, 1),
(13, '1', 'ACTIVO', '2025-11-16 18:06:42.598824', '1', 'Presencial', 'Regular', 1, 2, 14, 1, 1),
(14, '1', 'ACTIVO', '2025-11-11 06:50:46.248320', '1', 'Presencial', 'Regular', 1, 1, 2, 1, 1),
(15, '1', 'ACTIVO', '2025-11-11 06:50:46.321883', '1', 'Presencial', 'Regular', 1, 1, 3, 1, 1),
(16, '1', 'ACTIVO', '2025-11-11 06:50:46.330182', '1', 'Presencial', 'Regular', 1, 1, 4, 1, 1),
(17, '1', 'ACTIVO', '2025-11-11 06:50:46.345597', '1', 'Presencial', 'Regular', 1, 1, 5, 1, 1),
(18, '1', 'ACTIVO', '2025-11-11 06:50:46.347349', '1', 'Presencial', 'Regular', 1, 1, 6, 1, 1),
(19, '1', 'ACTIVO', '2025-11-11 06:50:46.364645', '1', 'Presencial', 'Regular', 1, 1, 7, 1, 1),
(20, '1', 'ACTIVO', '2025-11-11 06:50:46.378424', '1', 'Presencial', 'Regular', 1, 1, 8, 1, 1),
(21, '1', 'ACTIVO', '2025-11-11 06:50:46.381375', '1', 'Presencial', 'Regular', 1, 1, 9, 1, 1),
(22, '1', 'ACTIVO', '2025-11-11 06:50:46.397307', '1', 'Presencial', 'Regular', 1, 1, 10, 1, 1),
(23, '1', 'ACTIVO', '2025-11-11 06:50:46.397307', '1', 'Presencial', 'Regular', 1, 1, 11, 1, 1),
(24, '1', 'ACTIVO', '2025-11-11 06:50:46.419098', '1', 'Presencial', 'Regular', 1, 1, 12, 1, 1),
(25, '1', 'ACTIVO', '2025-11-11 06:50:46.421259', '1', 'Presencial', 'Regular', 1, 1, 13, 1, 1),
(26, '1', 'ACTIVO', '2025-11-11 06:50:46.427364', '1', 'Presencial', 'Regular', 1, 1, 14, 1, 1),
(27, '1', 'ACTIVO', '2025-11-23 23:30:49.974844', '1', 'Presencial', 'Regular', 1, 1, 19, 1, 1),
(28, '1', 'ACTIVO', '2025-11-23 23:30:50.123419', '1', 'Presencial', 'Regular', 1, 1, 20, 1, 1),
(29, '1', 'ACTIVO', '2025-11-23 23:30:50.232784', '1', 'Presencial', 'Regular', 1, 1, 21, 1, 1),
(30, '1', 'ACTIVO', '2025-11-23 23:30:50.327215', '1', 'Presencial', 'Regular', 1, 1, 22, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_periodo`
--

CREATE TABLE `upeu_periodo` (
  `id_periodo` bigint NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `estado` varchar(20) NOT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_inicio` date NOT NULL,
  `nombre` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_periodo`
--

INSERT INTO `upeu_periodo` (`id_periodo`, `descripcion`, `estado`, `fecha_fin`, `fecha_inicio`, `nombre`) VALUES
(1, 'Primer semestre 2025', 'FINALIZADO', '2025-07-31', '2025-03-01', '2025-I'),
(2, 'Segundo semestre 2025', 'ACTIVO', '2025-12-31', '2025-08-01', '2025-II'),
(3, 'Segundo semestre 2024', 'FINALIZADO', '2024-12-31', '2024-08-01', '2024-II'),
(4, 'Primer semestre del 2024', 'FINALIZADO', '2024-07-31', '2024-03-01', '2024-I');

-- --------------------------------------------------------

--
-- Table structure for table `upeu_persona`
--

CREATE TABLE `upeu_persona` (
  `id_persona` bigint NOT NULL,
  `celular` varchar(20) DEFAULT NULL,
  `codigo_estudiante` varchar(20) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `correo_institucional` varchar(100) DEFAULT NULL,
  `documento` varchar(20) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `foto` varchar(500) DEFAULT NULL,
  `nombre_completo` varchar(200) NOT NULL,
  `pais` varchar(50) DEFAULT NULL,
  `religion` varchar(50) DEFAULT NULL,
  `tipo_persona` enum('ESTUDIANTE','INVITADO') NOT NULL,
  `usuario_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_persona`
--

INSERT INTO `upeu_persona` (`id_persona`, `celular`, `codigo_estudiante`, `correo`, `correo_institucional`, `documento`, `fecha_nacimiento`, `foto`, `nombre_completo`, `pais`, `religion`, `tipo_persona`, `usuario_id`) VALUES
(1, '912345645', NULL, 'admin@upeu.edu.pe', NULL, '90000000', '2005-02-01', NULL, 'administrador', 'Perú', 'Adventista del séptimo día ', 'INVITADO', 1),
(2, '922070236', '202411775', '60178135@upeu.edu.pe', '60178135@upeu.edu.pe', '60178135', '2006-05-19', NULL, 'HUAÑACO QUISpe RENZO GABRIEL', 'Perú ', 'Catolico ', 'ESTUDIANTE', 2),
(3, '921838862', '202411776', '60178136@upeu.edu.pe', '60178136@upeu.edu.pe', '60178136', '2005-02-28', NULL, 'ZAPANA MAMANI LAURA ISABEL', 'Argentina', 'Adventista del Séptimo Día', 'ESTUDIANTE', 3),
(4, '921838863', '202411777', '60178137@upeu.edu.pe', '60178137@upeu.edu.pe', '60178137', '2006-09-15', NULL, 'COILA PAREDES MIGUEL ANGEL', 'Perú', 'Ninguno', 'ESTUDIANTE', 4),
(5, '921838864', '202411778', '60178138@upeu.edu.pe', '60178138@upeu.edu.pe', '60178138', '2005-12-03', NULL, 'VELASQUEZ CONDORI CAMILA FERNANDA', 'Perú', 'Evangélico', 'ESTUDIANTE', 5),
(6, '921838865', '202411779', '60178139@upeu.edu.pe', '60178139@upeu.edu.pe', '60178139', '2006-08-22', NULL, 'TICONA HUANCA RODRIGO JAVIER', 'Perú', 'Católico', 'ESTUDIANTE', 6),
(7, '921838866', '202411780', '60178140@upeu.edu.pe', '60178140@upeu.edu.pe', '60178140', '2006-04-14', NULL, 'APAZA QUISPE BRYAN STEVEN', 'Perú', 'Ninguno', 'ESTUDIANTE', 7),
(8, '921838867', '202411781', '60178141@upeu.edu.pe', '60178141@upeu.edu.pe', '60178141', '2005-07-07', NULL, 'MAMANI COILA DANIELA CAROLINA', 'Perú', 'Adventista del Séptimo Día', 'ESTUDIANTE', 8),
(9, '921838868', '202411782', '60178142@upeu.edu.pe', '60178142@upeu.edu.pe', '60178142', '2006-11-25', NULL, 'CASTRO FLORES JOSUE DAVID', 'Perú', 'Evangélico', 'ESTUDIANTE', 9),
(10, '921838869', '202411783', '60178143@upeu.edu.pe', '60178143@upeu.edu.pe', '60178143', '2006-03-18', NULL, 'CHURA YUCRA FABIOLA PATRICIA', 'Perú', 'Católico', 'ESTUDIANTE', 10),
(11, '921838870', '202411784', '60178144@upeu.edu.pe', '60178144@upeu.edu.pe', '60178144', '2005-10-30', NULL, 'HUANCA APAZA RICARDO MARTIN', 'Perú', 'Adventista del Séptimo Día', 'ESTUDIANTE', 11),
(12, '921838871', '202411785', '60178145@upeu.edu.pe', '60178145@upeu.edu.pe', '60178145', '2006-06-12', NULL, 'QUISPE TICONA LUIS FERNANDO', 'Perú', 'Católico', 'ESTUDIANTE', 12),
(13, '921838872', '202411786', '60178146@upeu.edu.pe', '60178146@upeu.edu.pe', '60178146', '2005-12-09', NULL, 'FLORES MAMANI SOFIA ALEJANDRA', 'Perú', 'Adventista del Séptimo Día', 'ESTUDIANTE', 13),
(14, '921838873', '202411787', '60178147@upeu.edu.pe', '60178147@upeu.edu.pe', '60178147', '2006-08-27', NULL, 'COAQUIRA HUANCA DIEGO ARMANDO', 'Perú', 'Ninguno', 'INVITADO', 14),
(15, '123456789', NULL, 'liders@upeu.edu.pe', NULL, '90000002', '2000-06-06', NULL, 'liders', '', '', 'INVITADO', 15),
(16, NULL, NULL, 'jesus.minecraft181219@gmail.com', NULL, '75978715', NULL, NULL, 'Leonardo Jesus Huaman Arhuata ', NULL, NULL, 'INVITADO', 16),
(17, NULL, NULL, 'lider@upeu.edu.pe', NULL, '90000009', NULL, NULL, 'lider', NULL, NULL, 'INVITADO', 17),
(18, NULL, NULL, '', NULL, '', NULL, NULL, 'Alexandra Coila', NULL, NULL, 'INVITADO', 18),
(19, '935277841', '202320800', '75388130@upeu.edu.pe', '75388130@upeu.edu.pe', '75388130', '2006-01-21', '', 'Choquecota Centon Joseph Rodrigo', 'Perú', 'Adventista del Séptimo Día', 'ESTUDIANTE', 19),
(20, '946903345', '202411755', '62177497@upeu.edu.pe', '62177497@upeu.edu.pe', '62177497', '2006-11-16', '', 'CCAZA ATAMARI ANDY LEONEL', 'Perú', 'Adventista del Séptimo Día', 'ESTUDIANTE', 20),
(21, '951577514', '202411761', 'alvarocatacora14@gmail.com', '76008236@upeu.edu.pe', '76008236', '2005-05-10', '', 'Catacora Mamani Jimy Alvaro', 'Perú', 'Ninguno', 'ESTUDIANTE', 21),
(22, '921838850', '202411764', '60178124@upeu.edu.pe', '60178124@upeu.edu.pe', '60178124', '2006-07-21', '', 'YANARICO COAQUIRA YEFER GUZMAN', 'Perú', 'Católico', 'ESTUDIANTE', 22);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_programa_estudio`
--

CREATE TABLE `upeu_programa_estudio` (
  `id_programa` bigint NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `facultad_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_programa_estudio`
--

INSERT INTO `upeu_programa_estudio` (`id_programa`, `descripcion`, `nombre`, `facultad_id`) VALUES
(1, 'EP Ingeniería de Sistemas', 'EP Ingeniería de Sistemas', 1);

-- --------------------------------------------------------

--
-- Table structure for table `upeu_roles`
--

CREATE TABLE `upeu_roles` (
  `id_rol` bigint NOT NULL,
  `descripcion` varchar(120) NOT NULL,
  `nombre` enum('ADMIN','INTEGRANTE','LIDER','SUPERADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_roles`
--

INSERT INTO `upeu_roles` (`id_rol`, `descripcion`, `nombre`) VALUES
(1, 'Super Administrador - Acceso total al sistema', 'SUPERADMIN'),
(2, 'Administrador - Gestión de matrículas, sedes, facultades y programas', 'ADMIN'),
(3, 'Líder - Acceso a dashboard de líder', 'LIDER'),
(4, 'Integrante - Acceso a dashboard de integrante', 'INTEGRANTE');

-- --------------------------------------------------------

--
-- Table structure for table `upeu_sede`
--

CREATE TABLE `upeu_sede` (
  `id_sede` bigint NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_sede`
--

INSERT INTO `upeu_sede` (`id_sede`, `descripcion`, `nombre`) VALUES
(1, 'Filial Juliaca', 'Filial Juliaca');

-- --------------------------------------------------------

--
-- Table structure for table `upeu_usuario`
--

CREATE TABLE `upeu_usuario` (
  `id_usuario` bigint NOT NULL,
  `clave` varchar(100) NOT NULL,
  `estado` varchar(10) NOT NULL,
  `user` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_usuario`
--

INSERT INTO `upeu_usuario` (`id_usuario`, `clave`, `estado`, `user`) VALUES
(1, '$2a$10$fkjyaVzhCPWzy.7P6felTuyKVVXJhdqLGGgcHEz52ZWt/JN8RAQ02', 'ACTIVO', 'admin'),
(2, '$2a$10$qEmC8nu9pCrB/jISt79CBeVtlxLBqbnWDZphmusKn3AcpNNOgSsUC', 'ACTIVO', 'renzo.huañaco'),
(3, '$2a$10$V9hGFr4XQ8.SIkdjWX8hSundsnY2TJhxLG6HVjOH/ZenImJ8ku1r2', 'ACTIVO', 'laura.zapana'),
(4, '$2a$10$rzrDklvnAwZfG0N7ZCkPUeZ.yQBYnc5IANz/i5Wm8MJXZNBGaVAye', 'ACTIVO', 'miguel.coila'),
(5, '$2a$10$jP1lPFO5.DQkSxyW0Q6RuOKRzy4ZcOAp83KOEBJ0u61oyHlbbV/jO', 'ACTIVO', 'camila.velasquez'),
(6, '$2a$10$/WZ4UL2Qy3U6Jg47QL71gufy1Cx5cwRv33mBWYvm4ApcyiPY6bEe2', 'ACTIVO', 'rodrigo.ticona'),
(7, '$2a$10$BfhjMqFQt1MhxpexVLF/b.ev75/2fTQWO3l9TvJtP3qDueqR7/01i', 'ACTIVO', 'bryan.apaza'),
(8, '$2a$10$ZeEk0o8DCuqWGtUGUYzVMOa2ps8P08oSKd3rULhb7tEjIpW/T3ewm', 'ACTIVO', 'daniela.mamani'),
(9, '$2a$10$W442PzXvTKzQiNllm2FDaObA5kkHTG2xwBOYvqsdLuHgaTC7rCaqG', 'ACTIVO', 'josue.castro'),
(10, '$2a$10$rHoQChJQOqFhOL7W2/1rIOlKXQ1CJfI0ugNpOjc1qW0rcEbl6aTfW', 'ACTIVO', 'fabiola.chura'),
(11, '$2a$10$Pa5ghVuRmVrsV8hxfPef2OC5nK4vB2Th4zd7c1.gbMmkgs/rz1GQW', 'ACTIVO', 'ricardo.huanca'),
(12, '$2a$10$g9nuxGo5buzbAbIK02aMbeQOaoq82sW59dsQc.TZnPowksNy6lK3G', 'ACTIVO', 'luis.quispe'),
(13, '$2a$10$i2fngm/bU6nxc8EcBgZGfuqRj5i/JD8T89A4CjXdwXRY64b3M3rOa', 'ACTIVO', 'sofia.flores'),
(14, '$2a$10$NpPwSw6W0JVthXm.DkTk8eKgGi03isOmz/W.TtJveorStjrHW2OLO', 'ACTIVO', 'diego.coaquira'),
(15, '$2a$10$SCrCmqdUXUF.0Ql7fZ4YEuX6aSNT.8/V4VbBrzuHOAcNzPdy2FEiy', 'ACTIVO', 'lider2'),
(16, '$2a$10$9valr2Wu0lMrRNg7uyyVC.Zk1hFEx.dN79/cQpUGBxMtVzbkmXGL.', 'ACTIVO', 'LeoNarD'),
(17, '$2a$10$Sv5jMV64QAGT9jgTnXPHNeqm/0XhIJVPkVKljht7dV..gVEP6Q8Fu', 'ACTIVO', 'lider'),
(18, '$2a$10$yujmMR9U1Nof1S49LlIyEO025ncsSR3GLO/yHyJF0k1oqIRh0NiAG', 'ACTIVO', 'Ale'),
(19, '$2a$10$CU8Qpzlcm6QIDUsTk621f.Jav9gKDs7pUzhTB4uha7Rm4fnZsIlQC', 'ACTIVO', '75388130'),
(20, '$2a$10$qHnRIyUQET3nrD38B2ezy.cyWvwD4mHUWkmiHd5WXWt6lTyTZHwK2', 'ACTIVO', 'andy.ccaza'),
(21, '$2a$10$oGYw2JlHC4Q4.HIxUGkgTe5gZBN7Omu2Ew7ENPsJCqRCR2diNugZy', 'ACTIVO', 'jimy.catacora'),
(22, '$2a$10$vMeqYvtvD23rhabotI3w8.RJpskWikJxiHqiwarN27t.YvfG6JTn6', 'ACTIVO', '60178124');

-- --------------------------------------------------------

--
-- Table structure for table `upeu_usuario_rol`
--

CREATE TABLE `upeu_usuario_rol` (
  `rol_id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `upeu_usuario_rol`
--

INSERT INTO `upeu_usuario_rol` (`rol_id`, `usuario_id`) VALUES
(2, 1),
(4, 2),
(4, 3),
(4, 4),
(4, 5),
(4, 6),
(4, 7),
(4, 8),
(4, 9),
(4, 10),
(4, 11),
(4, 12),
(4, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(1, 18),
(4, 19),
(4, 20),
(4, 21),
(4, 22);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `upeu_accesos`
--
ALTER TABLE `upeu_accesos`
  ADD PRIMARY KEY (`id_acceso`),
  ADD KEY `FKljfx5lkc3dss4ge4qn69cp1qu` (`id_acceso_padre`);

--
-- Indexes for table `upeu_acceso_rol`
--
ALTER TABLE `upeu_acceso_rol`
  ADD PRIMARY KEY (`acceso_id`,`rol_id`),
  ADD KEY `FK5olgwvkvi6gfmjp117cnq02cc` (`rol_id`);

--
-- Indexes for table `upeu_asistencia`
--
ALTER TABLE `upeu_asistencia`
  ADD PRIMARY KEY (`id_asistencia`),
  ADD UNIQUE KEY `UKawk4i32s5the3qh69cibxqkrt` (`evento_especifico_id`,`persona_id`),
  ADD KEY `FKpxkhed4xv3arkivkfdq9ikjr3` (`persona_id`);

--
-- Indexes for table `upeu_evento_especifico`
--
ALTER TABLE `upeu_evento_especifico`
  ADD PRIMARY KEY (`id_evento_especifico`),
  ADD KEY `FK12wc9476lus5b8cytlyaoma6y` (`evento_general_id`);

--
-- Indexes for table `upeu_evento_general`
--
ALTER TABLE `upeu_evento_general`
  ADD PRIMARY KEY (`id_evento_general`),
  ADD KEY `FKaqj4n1hie57fdtms0m9ve7crj` (`periodo_id`),
  ADD KEY `FKe81eufrr2oui2ihc1fii19bj8` (`programa_id`);

--
-- Indexes for table `upeu_facultad`
--
ALTER TABLE `upeu_facultad`
  ADD PRIMARY KEY (`id_facultad`),
  ADD UNIQUE KEY `UKr4ltlc1rojwdr25ctjpo5ljen` (`nombre`),
  ADD KEY `FKf9yprpkbqbdkpriq54hfocu1m` (`id_sede`);

--
-- Indexes for table `upeu_grupo_general`
--
ALTER TABLE `upeu_grupo_general`
  ADD PRIMARY KEY (`id_grupo_general`),
  ADD KEY `FKl8nf1atpub1xsfwwe0445yodq` (`evento_general_id`);

--
-- Indexes for table `upeu_grupo_participante`
--
ALTER TABLE `upeu_grupo_participante`
  ADD PRIMARY KEY (`id_grupo_participante`),
  ADD UNIQUE KEY `UKs351d1ibnw39bp0ui1tjelf22` (`grupo_pequeno_id`,`persona_id`),
  ADD KEY `FK1js80x3m89l0913g51fu9n45g` (`persona_id`);

--
-- Indexes for table `upeu_grupo_pequeno`
--
ALTER TABLE `upeu_grupo_pequeno`
  ADD PRIMARY KEY (`id_grupo_pequeno`),
  ADD KEY `FKkt4b100k8714etlpn5hi4sht` (`grupo_general_id`),
  ADD KEY `FK73hhhtousjlpcu598uddcgd0a` (`lider_id`);

--
-- Indexes for table `upeu_matricula`
--
ALTER TABLE `upeu_matricula`
  ADD PRIMARY KEY (`id_matricula`),
  ADD KEY `FK7awilfnvri2qwojcwrp6ahh42` (`facultad_id`),
  ADD KEY `FKbl6tkqrinfnkseimi8o4s3mvy` (`periodo_id`),
  ADD KEY `FKrka3gnhkh2ndye2c4lv7ioxl8` (`persona_id`),
  ADD KEY `FKi3gcktb2mjnbi7uejv79g3eod` (`programa_id`),
  ADD KEY `FK4twl184v124wty2vhuek0lvp6` (`sede_id`);

--
-- Indexes for table `upeu_periodo`
--
ALTER TABLE `upeu_periodo`
  ADD PRIMARY KEY (`id_periodo`),
  ADD UNIQUE KEY `UKif4oxan3pkuu8hovj0tsuu6j7` (`nombre`);

--
-- Indexes for table `upeu_persona`
--
ALTER TABLE `upeu_persona`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `UKlxoymbr59itrbf6sjaw9to5to` (`codigo_estudiante`),
  ADD UNIQUE KEY `UKaf0lk6c2hkk600sjp80y1j8it` (`documento`),
  ADD UNIQUE KEY `UK16e70r46fswii1afns4i9wvmq` (`usuario_id`);

--
-- Indexes for table `upeu_programa_estudio`
--
ALTER TABLE `upeu_programa_estudio`
  ADD PRIMARY KEY (`id_programa`),
  ADD UNIQUE KEY `UKopjcq007j3v306qek5xvbc3ac` (`nombre`),
  ADD KEY `FKsrkcs3278ksxs8of0vd4iwu8o` (`facultad_id`);

--
-- Indexes for table `upeu_roles`
--
ALTER TABLE `upeu_roles`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `UK3hu3jg08dv5v4g9mhobni9kxm` (`nombre`);

--
-- Indexes for table `upeu_sede`
--
ALTER TABLE `upeu_sede`
  ADD PRIMARY KEY (`id_sede`),
  ADD UNIQUE KEY `UKrf48l6lcylhbvh4l3o7adv24b` (`nombre`);

--
-- Indexes for table `upeu_usuario`
--
ALTER TABLE `upeu_usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `UKnglinedpa99qfym8jreg27mx8` (`user`);

--
-- Indexes for table `upeu_usuario_rol`
--
ALTER TABLE `upeu_usuario_rol`
  ADD PRIMARY KEY (`rol_id`,`usuario_id`),
  ADD KEY `FKehuc4c37b7soxfqfexqh3kg6s` (`usuario_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `upeu_accesos`
--
ALTER TABLE `upeu_accesos`
  MODIFY `id_acceso` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `upeu_asistencia`
--
ALTER TABLE `upeu_asistencia`
  MODIFY `id_asistencia` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `upeu_evento_especifico`
--
ALTER TABLE `upeu_evento_especifico`
  MODIFY `id_evento_especifico` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `upeu_evento_general`
--
ALTER TABLE `upeu_evento_general`
  MODIFY `id_evento_general` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `upeu_facultad`
--
ALTER TABLE `upeu_facultad`
  MODIFY `id_facultad` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `upeu_grupo_general`
--
ALTER TABLE `upeu_grupo_general`
  MODIFY `id_grupo_general` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `upeu_grupo_participante`
--
ALTER TABLE `upeu_grupo_participante`
  MODIFY `id_grupo_participante` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT for table `upeu_grupo_pequeno`
--
ALTER TABLE `upeu_grupo_pequeno`
  MODIFY `id_grupo_pequeno` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `upeu_matricula`
--
ALTER TABLE `upeu_matricula`
  MODIFY `id_matricula` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `upeu_periodo`
--
ALTER TABLE `upeu_periodo`
  MODIFY `id_periodo` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `upeu_persona`
--
ALTER TABLE `upeu_persona`
  MODIFY `id_persona` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `upeu_programa_estudio`
--
ALTER TABLE `upeu_programa_estudio`
  MODIFY `id_programa` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `upeu_roles`
--
ALTER TABLE `upeu_roles`
  MODIFY `id_rol` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `upeu_sede`
--
ALTER TABLE `upeu_sede`
  MODIFY `id_sede` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `upeu_usuario`
--
ALTER TABLE `upeu_usuario`
  MODIFY `id_usuario` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `upeu_accesos`
--
ALTER TABLE `upeu_accesos`
  ADD CONSTRAINT `FKljfx5lkc3dss4ge4qn69cp1qu` FOREIGN KEY (`id_acceso_padre`) REFERENCES `upeu_accesos` (`id_acceso`);

--
-- Constraints for table `upeu_acceso_rol`
--
ALTER TABLE `upeu_acceso_rol`
  ADD CONSTRAINT `FK5olgwvkvi6gfmjp117cnq02cc` FOREIGN KEY (`rol_id`) REFERENCES `upeu_roles` (`id_rol`),
  ADD CONSTRAINT `FKpt947hoat26u0hbc4qmkrfwj2` FOREIGN KEY (`acceso_id`) REFERENCES `upeu_accesos` (`id_acceso`);

--
-- Constraints for table `upeu_asistencia`
--
ALTER TABLE `upeu_asistencia`
  ADD CONSTRAINT `FKpno76wlamlwr67xn8uy1wdoj` FOREIGN KEY (`evento_especifico_id`) REFERENCES `upeu_evento_especifico` (`id_evento_especifico`),
  ADD CONSTRAINT `FKpxkhed4xv3arkivkfdq9ikjr3` FOREIGN KEY (`persona_id`) REFERENCES `upeu_persona` (`id_persona`);

--
-- Constraints for table `upeu_evento_especifico`
--
ALTER TABLE `upeu_evento_especifico`
  ADD CONSTRAINT `FK12wc9476lus5b8cytlyaoma6y` FOREIGN KEY (`evento_general_id`) REFERENCES `upeu_evento_general` (`id_evento_general`);

--
-- Constraints for table `upeu_evento_general`
--
ALTER TABLE `upeu_evento_general`
  ADD CONSTRAINT `FKaqj4n1hie57fdtms0m9ve7crj` FOREIGN KEY (`periodo_id`) REFERENCES `upeu_periodo` (`id_periodo`),
  ADD CONSTRAINT `FKe81eufrr2oui2ihc1fii19bj8` FOREIGN KEY (`programa_id`) REFERENCES `upeu_programa_estudio` (`id_programa`);

--
-- Constraints for table `upeu_facultad`
--
ALTER TABLE `upeu_facultad`
  ADD CONSTRAINT `FKf9yprpkbqbdkpriq54hfocu1m` FOREIGN KEY (`id_sede`) REFERENCES `upeu_sede` (`id_sede`);

--
-- Constraints for table `upeu_grupo_general`
--
ALTER TABLE `upeu_grupo_general`
  ADD CONSTRAINT `FKl8nf1atpub1xsfwwe0445yodq` FOREIGN KEY (`evento_general_id`) REFERENCES `upeu_evento_general` (`id_evento_general`);

--
-- Constraints for table `upeu_grupo_participante`
--
ALTER TABLE `upeu_grupo_participante`
  ADD CONSTRAINT `FK1js80x3m89l0913g51fu9n45g` FOREIGN KEY (`persona_id`) REFERENCES `upeu_persona` (`id_persona`),
  ADD CONSTRAINT `FKr0ykykwd7o050hmqwb2d60ej7` FOREIGN KEY (`grupo_pequeno_id`) REFERENCES `upeu_grupo_pequeno` (`id_grupo_pequeno`);

--
-- Constraints for table `upeu_grupo_pequeno`
--
ALTER TABLE `upeu_grupo_pequeno`
  ADD CONSTRAINT `FK73hhhtousjlpcu598uddcgd0a` FOREIGN KEY (`lider_id`) REFERENCES `upeu_persona` (`id_persona`),
  ADD CONSTRAINT `FKkt4b100k8714etlpn5hi4sht` FOREIGN KEY (`grupo_general_id`) REFERENCES `upeu_grupo_general` (`id_grupo_general`);

--
-- Constraints for table `upeu_matricula`
--
ALTER TABLE `upeu_matricula`
  ADD CONSTRAINT `FK4twl184v124wty2vhuek0lvp6` FOREIGN KEY (`sede_id`) REFERENCES `upeu_sede` (`id_sede`),
  ADD CONSTRAINT `FK7awilfnvri2qwojcwrp6ahh42` FOREIGN KEY (`facultad_id`) REFERENCES `upeu_facultad` (`id_facultad`),
  ADD CONSTRAINT `FKbl6tkqrinfnkseimi8o4s3mvy` FOREIGN KEY (`periodo_id`) REFERENCES `upeu_periodo` (`id_periodo`),
  ADD CONSTRAINT `FKi3gcktb2mjnbi7uejv79g3eod` FOREIGN KEY (`programa_id`) REFERENCES `upeu_programa_estudio` (`id_programa`),
  ADD CONSTRAINT `FKrka3gnhkh2ndye2c4lv7ioxl8` FOREIGN KEY (`persona_id`) REFERENCES `upeu_persona` (`id_persona`);

--
-- Constraints for table `upeu_persona`
--
ALTER TABLE `upeu_persona`
  ADD CONSTRAINT `FKdcoevt6hru00vhxxuakg6yk89` FOREIGN KEY (`usuario_id`) REFERENCES `upeu_usuario` (`id_usuario`);

--
-- Constraints for table `upeu_programa_estudio`
--
ALTER TABLE `upeu_programa_estudio`
  ADD CONSTRAINT `FKsrkcs3278ksxs8of0vd4iwu8o` FOREIGN KEY (`facultad_id`) REFERENCES `upeu_facultad` (`id_facultad`);

--
-- Constraints for table `upeu_usuario_rol`
--
ALTER TABLE `upeu_usuario_rol`
  ADD CONSTRAINT `FK5i9y5d4yvar0uhcf6q50qpw9h` FOREIGN KEY (`rol_id`) REFERENCES `upeu_roles` (`id_rol`),
  ADD CONSTRAINT `FKehuc4c37b7soxfqfexqh3kg6s` FOREIGN KEY (`usuario_id`) REFERENCES `upeu_usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
