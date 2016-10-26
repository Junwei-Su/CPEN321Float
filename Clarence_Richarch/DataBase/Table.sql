CREATE TABLE User(user_id INT, name VARCHAR(30), account VARCHAR(30),
	address VARCHAR(30));
	
CREATE TABLE Campaign(campaign_id INT, name VARCHAR(100), initializer INT,
	description VARCHAR(1000), initial_location VARCHAR(30), destination VARCHAR(30), pledge_amount INT);
	
CREATE TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_id INT, 
	associated_user_id INT, date DATE, location VARCHAR(30), user_id INT);
	
CREATE TABLE camp_follower_map(user_id INT, campaign_id INT, date DATE);

CREATE TABLE camp_owner_map(user_id INT, campaign_id INT, date DATE);


INSERT INTO User (user_id, name, account, address) VALUES (1, 'Clarence', 'float1', 'UBC');
INSERT INTO User (user_id, name, account, address) VALUES (2, 'Richard', 'float2', 'UBC');