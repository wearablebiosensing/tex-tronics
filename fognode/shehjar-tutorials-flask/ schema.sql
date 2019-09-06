
CREATE TABLE IF NOT EXISTS patients(
        id INT AUTO_INCREMENT PRIMARY KEY, 
        name VARCHAR, email VARCHAR, password VARCHAR)

CREATE TABLE IF NOT EXISTS doctors(
    id INT AUTO_INCREMENT PRIMARY KEY, 
    name VARCHAR, email VARCHAR, password VARCHAR )

CREATE TABLE "fingerTap" (
        "id" INT
	"Thumb"	INTEGER,
	"Ring"	INTEGER,
	"Acc(x)"	INTEGER,
	"Acc(y)"	INTEGER,
	"Acc(z)"	INTEGER,
	"Gyr(x)"	INTEGER,
	"Gyr(y)"	INTEGER,
	"Gyr(z)"	INTEGER,
	"Device Address"	TEXT,
	"Exercise"	TEXT
        "Patient ID"  INTEGER,
        "Doctor ID" INTEGER
);
CREATE TABLE "resting_hands_on_thighs" (
        "id" INT
	"Thumb"	INTEGER,
	"Ring"	INTEGER,
	"Acc(x)"	INTEGER,
	"Acc(y)"	INTEGER,
	"Acc(z)"	INTEGER,
	"Gyr(x)"	INTEGER,
	"Gyr(y)"	INTEGER,
	"Gyr(z)"	INTEGER,
	"Device Address"	TEXT,
	"Exercise"	TEXT
        "Patient ID"  INTEGER,
        "Doctor ID" INTEGER
);

CREATE TABLE "HandFlip" (
        "id" INT
	"Thumb"	INTEGER,
	"Ring"	INTEGER,
	"Acc(x)"	INTEGER,
	"Acc(y)"	INTEGER,
	"Acc(z)"	INTEGER,
	"Gyr(x)"	INTEGER,
	"Gyr(y)"	INTEGER,
	"Gyr(z)"	INTEGER,
	"Device Address"	TEXT,
	"Exercise"	TEXT
        "Patient ID"  INTEGER,
        "Doctor ID" INTEGER
);

CREATE TABLE "HoldingHandsOut" (
        "id" INT
	"Thumb"	INTEGER,
	"Ring"	INTEGER,
	"Acc(x)"	INTEGER,
	"Acc(y)"	INTEGER,
	"Acc(z)"	INTEGER,
	"Gyr(x)"	INTEGER,
	"Gyr(y)"	INTEGER,
	"Gyr(z)"	INTEGER,
	"Device Address"	TEXT,
	"Exercise"	TEXT
        "Patient ID"  INTEGER,
        "Doctor ID" INTEGER
);
CREATE TABLE "HoldingHandsOut" (
        "id" INT
	"Thumb"	INTEGER,
	"Ring"	INTEGER,
	"Acc(x)"	INTEGER,
	"Acc(y)"	INTEGER,
	"Acc(z)"	INTEGER,
	"Gyr(x)"	INTEGER,
	"Gyr(y)"	INTEGER,
	"Gyr(z)"	INTEGER,
	"Device Address"	TEXT,
	"Exercise"	TEXT
        "Patient ID"  INTEGER,
        "Doctor ID" INTEGER
);