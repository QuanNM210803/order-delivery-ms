-- Bảng USER
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(10) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `address` VARCHAR(255),
    `is_verified` BOOLEAN NOT NULL DEFAULT FALSE,

    is_active TINYINT(1),
    is_deleted TINYINT(1),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ROLE
CREATE TABLE `role` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    `description` TEXT,

    is_active TINYINT(1),
    is_deleted TINYINT(1),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng trung gian USER_ROLES (Many-to-Many)
CREATE TABLE `user_roles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,

    is_active TINYINT(1),
    is_deleted TINYINT(1),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(255),
updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng DELIVERY_STAFF
CREATE TABLE `delivery_staff` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `finding_order` BOOLEAN NOT NULL,
    CONSTRAINT `fk_delivery_staff_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,

    is_active TINYINT(1),
    is_deleted TINYINT(1),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;