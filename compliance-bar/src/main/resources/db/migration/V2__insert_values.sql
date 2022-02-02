INSERT INTO USERS (username, password, role) VALUES
     ('Tasos', '$2a$10$mHp/pf..OZt0ezy9uIc.deWTuQGswzwmPNVaX.zVBZ24wiyE3UCva', 'ROLE_USER'),
     ('Kent', '$2a$10$0qI5DJv85tPam/lLdk1W2emabdAPY38KlUK7PqOTOXpIM/RG/wxFm', 'ROLE_COMPLIANCE_OFFICER');


INSERT INTO COMPLIANCE_SHOTS (ID, TITLE, SHORT_DESC, REF_URL, TUTORIAL_URL, COMPLIANCE_LEVEL, MIN_AVAILABILITY_R, MIN_INTEGRITY_R, MIN_CONFIDENTIALITY_R, CREATED_BY) VALUES
    ('3515466b-de86-42ca-a609-a23c790343d2', 'Message payload signing', 'Applications must sign their requests and validate the signatures of the responses they receive. Keys for signing must be stored in a FIPS 140-2 certified HSM', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/signing', 'BLOCKING', '1', '2', '1', 'Tasos'),
    ('4b5db435-2635-47fa-b1bf-ed2197cef427', 'Data in transit protection', 'Applications must secure their communications. In this shot we define the protocols, algorithms, cipher suites and key lengths required by our policies. Also how to properly validate certificates.', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/tls', 'BLOCKING', '1', '1', '1', 'Tasos'),
    ('10ebe541-2385-416e-a139-db278deff548', 'Secrets protection', 'Applications require multiple secrets such as username/passwords, keys for TLS and for signing, etc. In this shot we specify which secret stores are allowed for each asset (secret) and use case. Also their proper handling and lifecycle.', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/secrets', 'BLOCKING', '1', '1', '1', 'Tasos'),
    ('8ba1441f-a874-404d-9d47-27964ae99423', 'Controlled & auditable access to test/production envs', 'Manual access to production is forbidden and should be disabled. All changes must be done via a compliant pipeline defined as code, which is reviewed. Pipeline must include reviewing of changes, testing, utilization of sast and dast tools and approval by business to complete', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/systems-access-management', 'BLOCKING', '1', '1', '1', 'Tasos'),
    ('da927180-a991-4f8d-8927-ad4e917b739d', 'Compliant pipelines', 'Pipelines must meet certain criteria to be compliant. Pipelines are scanned for compliance against these criteria.', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/compliant-pipelines', 'BLOCKING', '1', '1', '1', 'Tasos'),
    ('1740b849-4d09-4a20-819e-925dc042ed99', 'Hardened and monitored configuration.', 'All default authentication values need to be replaced with strong alternatives. Changes in configuration must be done only via pipelines. Periodic automated review and comparison of expected configurations and actual configurations must be in place.', 'https://www.yourwiki.com/policies', 'https://www.yourwiki.com/env-hardening', 'BLOCKING', '1', '1', '1', 'Tasos');

INSERT INTO CONFIGURATION_ITEMS (ID, EXTERNAL_ID, CI_NAME, SYSTEM_OWNER, TEAM_EMAIL, EXTERNAL_CMDB_NAME) VALUES
    ('dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957', '10fe8faf-65c3-418c-aae8-2cb57f0183e7', 'backstage', 'Alan', 'alan.turing@somemail.com', 'MyCMDB'),
    ('ae47c77baa96ef3ba0b030a5b46d8e499b9eb040a897c66a0e62651f9149d401', 'afba047a-14da-45bc-9e3c-a86ac5dfc2b3', 'luigi', 'Ada', 'ada.lovelace@somemail.com', 'MyCMDB'),
    ('c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17', '8bfcaff1-ef34-4ade-b181-9a0c66dc2f7f', 'apollo', 'Ada', 'ada.lovelace@somemail.com', 'MyCMDB'),
    ('5acf250dd9a9959c6bc8034b97912ecda5916911f919a62ad2dd5f959ff55142', '2806df12-8411-4f8e-853a-3d774ef36be0', 'ratatool', 'Grace', 'grace.hopper@somemail.com', 'MyCMDB'),
    ('c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e', '846e1b50-7c26-4c85-8f94-29e7837d9713', 'zoltar', 'Alan', 'alan.turing@somemail.com', 'MyCMDB'),
    ('4fa7278921537e02a4d488f7afa395c5278a8ff82ac784d7cd03a1c6503408f7', '67a2f077-4a76-4e62-ae11-420739ce9db5', 'talos', 'Tim', 'tbl@somemail.com', 'MyCMDB');

INSERT INTO COMPLIANCE_SHOTS_CONFIGURATION_ITEMS (STATUS, COMPLIANCE_SHOT_ID, CONFIGURATION_ITEM_ID) VALUES
    ('CREATED', '8ba1441f-a874-404d-9d47-27964ae99423', 'dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957'),
    ('SUBMITTED', '8ba1441f-a874-404d-9d47-27964ae99423', 'ae47c77baa96ef3ba0b030a5b46d8e499b9eb040a897c66a0e62651f9149d401'),
    ('CREATED', '8ba1441f-a874-404d-9d47-27964ae99423', 'c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17'),
    ('CREATED', '8ba1441f-a874-404d-9d47-27964ae99423', '5acf250dd9a9959c6bc8034b97912ecda5916911f919a62ad2dd5f959ff55142'),
    ('SUBMITTED', '8ba1441f-a874-404d-9d47-27964ae99423', 'c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e'),
    ('CREATED', '8ba1441f-a874-404d-9d47-27964ae99423', '4fa7278921537e02a4d488f7afa395c5278a8ff82ac784d7cd03a1c6503408f7'),
    ('COMPLETED', '4b5db435-2635-47fa-b1bf-ed2197cef427', 'dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957'),
    ('CREATED', '4b5db435-2635-47fa-b1bf-ed2197cef427', 'ae47c77baa96ef3ba0b030a5b46d8e499b9eb040a897c66a0e62651f9149d401'),
    ('SUBMITTED', '4b5db435-2635-47fa-b1bf-ed2197cef427', 'c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17'),
    ('CREATED', '4b5db435-2635-47fa-b1bf-ed2197cef427', '5acf250dd9a9959c6bc8034b97912ecda5916911f919a62ad2dd5f959ff55142'),
    ('CANCELLED', '4b5db435-2635-47fa-b1bf-ed2197cef427', 'c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e'),
    ('SUBMITTED', '4b5db435-2635-47fa-b1bf-ed2197cef427', '4fa7278921537e02a4d488f7afa395c5278a8ff82ac784d7cd03a1c6503408f7'),
    ('CREATED', 'da927180-a991-4f8d-8927-ad4e917b739d', 'dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957'),
    ('SUBMITTED', 'da927180-a991-4f8d-8927-ad4e917b739d', 'ae47c77baa96ef3ba0b030a5b46d8e499b9eb040a897c66a0e62651f9149d401'),
    ('CREATED', 'da927180-a991-4f8d-8927-ad4e917b739d', 'c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17'),
    ('COMPLETED', 'da927180-a991-4f8d-8927-ad4e917b739d', '5acf250dd9a9959c6bc8034b97912ecda5916911f919a62ad2dd5f959ff55142'),
    ('CREATED', 'da927180-a991-4f8d-8927-ad4e917b739d', 'c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e'),
    ('CREATED', 'da927180-a991-4f8d-8927-ad4e917b739d', '4fa7278921537e02a4d488f7afa395c5278a8ff82ac784d7cd03a1c6503408f7'),
    ('SUBMITTED', '10ebe541-2385-416e-a139-db278deff548', 'dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957'),
    ('CREATED', '10ebe541-2385-416e-a139-db278deff548', 'ae47c77baa96ef3ba0b030a5b46d8e499b9eb040a897c66a0e62651f9149d401'),
    ('COMPLETED', '10ebe541-2385-416e-a139-db278deff548', 'c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17'),
    ('CREATED', '10ebe541-2385-416e-a139-db278deff548', '5acf250dd9a9959c6bc8034b97912ecda5916911f919a62ad2dd5f959ff55142'),
    ('CREATED', '10ebe541-2385-416e-a139-db278deff548', 'c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e'),
    ('SUBMITTED', '10ebe541-2385-416e-a139-db278deff548', '4fa7278921537e02a4d488f7afa395c5278a8ff82ac784d7cd03a1c6503408f7'),
    ('CANCELLED', '3515466b-de86-42ca-a609-a23c790343d2', 'dd9f6c1074cc4c98fa88c719204d918b765767b4a26825827cdd7a2db275b957'),
    ('CREATED', '3515466b-de86-42ca-a609-a23c790343d2', 'c3a563bab07a9d5088253b4f1cc6c33be7055d577a4565822f125863e8840a17'),
    ('COMPLETED', '3515466b-de86-42ca-a609-a23c790343d2', 'c77fdbce45c126a15427f4c47af79ecdd67d29a455cc6dbd4c89c8ce7058516e');


