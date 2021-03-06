CREATE VIEW `SIGBYDATEVIEW` AS(
SELECT
	COUNT(*) AS SOS,
	MONTH(dateOfSignature) AS MONTH,
	YEAR(dateOfSignature) AS YEAR
FROM OCT_SIGNATURE s, OCT_SYSTEM_PREFS sys
WHERE s.DATEOFSIGNATURE BETWEEN sys.REGISTRATIONDATE AND sys.DEADLINE
GROUP BY YEAR, MONTH
);


CREATE VIEW `SIGBYCOUNTRYVIEW`  AS(
SELECT
	COUNT(*) AS SOS,
	MONTH(s.dateOfSignature) AS MONTH,
	YEAR(s.dateOfSignature) AS YEAR,
	CODE AS CODE
	FROM OCT_SIGNATURE s,OCT_COUNTRY c, OCT_SYSTEM_PREFS sys
WHERE CODE = (SELECT CODE FROM OCT_COUNTRY WHERE id = countryToSignFor_id )
AND s.DATEOFSIGNATURE BETWEEN sys.REGISTRATIONDATE AND sys.DEADLINE
GROUP BY CODE, YEAR, MONTH
);

CREATE VIEW `LASTSIGNATURESVIEW` AS
(
SELECT id AS SIGNATUREID
FROM OCT_SIGNATURE ORDER BY id DESC LIMIT 5
);

/* fast signature count view */
CREATE VIEW FASTSIGNATURECOUNTVIEW AS
SELECT  c.id AS countryId, 
        COUNT(*) AS count
FROM  OCT_SIGNATURE s, OCT_COUNTRY c
WHERE c.CODE = (SELECT c1.CODE FROM OCT_COUNTRY c1 WHERE c1.id = s.countryToSignFor_id)
GROUP BY c.id