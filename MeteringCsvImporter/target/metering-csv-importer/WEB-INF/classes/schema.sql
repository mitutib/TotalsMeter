CREATE TABLE IF NOT EXISTS `bbeng_metering_reading_intervals` (
  `meter_id` INT NOT NULL,
  `interval_start` DATETIME NOT NULL,
  `timestamp` DATETIME NOT NULL,
  `interval_length` INT NOT NULL,
  `stream_suffix` VARCHAR(4) NOT NULL,
  `stream_number` INT NOT NULL,
  `uom` VARCHAR(16) NOT NULL,
  `quality` VARCHAR(2) NOT NULL,
  `method_flag` VARCHAR(8) NULL,
  `reason_code` INT NULL,
  `scaled` VARCHAR(2) NOT NULL,
  `value` DECIMAL(18,3) NOT NULL DEFAULT 0.000,
  UNIQUE KEY `uq_mri` (`meter_id`,`interval_start`,`timestamp`,`interval_length`,
                       `stream_suffix`,`stream_number`,`uom`,`quality`,
                       `method_flag`,`reason_code`,`scaled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `bbeng_metering_totals_meter` (
  `meter_id` INT NOT NULL,
  `total_value` DECIMAL(18,3) NOT NULL DEFAULT 0.000,
  PRIMARY KEY (`meter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
