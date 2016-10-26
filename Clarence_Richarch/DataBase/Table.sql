CREATE TABLE User(user_id INT, name VARCHAR(30), account VARCHAR(30),
	address VARCHAR(30));
	
CREATE TABLE Campaign(campaign_id INT, name VARCHAR(100), initializer INT,
	description VARCHAR(1000), initial_location VARCHAR(30), destination VARCHAR(30), pledge_amount INT, accumulated_donation INT);
	
CREATE TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_id INT, 
	associated_user_id INT, date DATE, location VARCHAR(30), user_id INT);
	
CREATE TABLE camp_follower_map(user_id INT, campaign_id INT, date DATE);

CREATE TABLE camp_owner_map(user_id INT, campaign_id INT, date DATE);

//test data for user table
INSERT INTO User (user_id, name, account, address) VALUES (1, 'Clarence', 'float1', 'UBC');
INSERT INTO User (user_id, name, account, address) VALUES (2, 'Richard', 'float2', 'UBC');
INSERT INTO User (user_id, name, account, address) VALUES (3, 'A', 'float3', 'SFU');
INSERT INTO User (user_id, name, account, address) VALUES (4, 'B', 'float4', 'SFU');
INSERT INTO User (user_id, name, account, address) VALUES (5, 'C', 'float5', 'UBC');
INSERT INTO User (user_id, name, account, address) VALUES (6, 'D', 'float6', 'UBC');


//test data for campaign table
INSERT INTO Campaign (campaign_id, name, initializer, description, initial_location, destination, pledge_amount, accumulated_donation) VALUES(1, 'testCamp1', 2, 'testing campaign', '10,20','3,3',1000,0); 
INSERT INTO Campaign (campaign_id, name, initializer, description, initial_location, destination, pledge_amount, accumulated_donation)VALUES(2, 'testCamp2', 3, 'testing campaign', '20,30','4,4',4000,500); 