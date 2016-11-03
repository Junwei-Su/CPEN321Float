-- CREATE TABLE User(user_id INT, name VARCHAR(30), account VARCHAR(30),
-- 	address VARCHAR(30));
	
-- CREATE TABLE Campaign(campaign_id INT, name VARCHAR(100), initializer INT,
-- 	description VARCHAR(1000), initial_location VARCHAR(30), destination VARCHAR(30), pledge_amount INT, accumulated_donation INT);
-- 	
-- 	
-- 	Title/name
-- Charity 
-- Goal amount
-- Goal destination
-- Pledge amount initial
-- Money raised so far
-- Decription
-- locations ( initial and points spread to)
-- User ( starter)
-- Date started
-- Time left
-- Date ended (if it end)

-- 
-- reating a user/charity account, we need to save for every account:
-- Name
-- PIcture
-- Date joined/added
-- Blurb ( personal description)
-- isCharity(flag)
-- If (isCharity) amount gained from Float campaigns should be displayed
-- Amount raised (money others have pledged from your campaigns
-- Amount donated
-- City,country of origin 

CREATE TABLE User(account VARCHAR(30), user_name VARCHAR(30), date_join DATE, blurb VARCHAR(1000), is_charity TINYINT,
	amount_gained INT, amount_raised INT, amount_donated INT, city VARCHAR(20), province VARCHAR(20), country VARCHAR(20));


CREATE TABLE Campaign(campaign_name VARCHAR(100), charity VARCHAR(100), goal_amount INT, 
initial_location VARCHAR(30), destination VARCHAR(30), owner_account VARCHAR(30), accumulated_donation INT, time_left INT)
	
CREATE TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_name INT,description VARCHAR(2000), 
	associated_user_name INT, date DATE, location VARCHAR(30));
	
CREATE TABLE camp_follower_map(account VARCHAR(30), campaign_name VARCHAR(100), date DATE);


-- test data for user table
INSERT INTO User (account, user_name, date_join, blurb, is_charity,amount_gained,amount_raised,
amount_donated,city,province, country) 
VALUES ('float1','A', '2016-10-1',"empty", 0, 0, 0, 0, 'Vancouver', 'BC', 'Canada');
INSERT INTO User (account, user_name, date_join, blurb, is_charity,amount_gained,amount_raised,
amount_donated,city,province,country) 
VALUES ('float2','B', '2016-10-1',"empty", 0, 0, 0, 0, 'Victoria', 'BC', 'Canada');
INSERT INTO User (account, user_name, date_join, blurb, is_charity,amount_gained,amount_raised,
amount_donated,city,province,country) 
VALUES ('float3','C', '2016-10-1',"empty", 0, 0, 0, 0, 'Richmond', 'BC', 'Canada');
INSERT INTO User (account, user_name, date_join, blurb, is_charity,amount_gained,amount_raised,
amount_donated,city,province,country) 
VALUES ('float4','D', '2016-10-1',"empty", 0, 0, 0, 0, 'Vancouver', 'BC', 'Canada');


-- test data for campaign table
INSERT INTO Campaign (campaign_name, charity, goal_amount, initial_location, destination, 
owner_account, accumulated_donation, time_left, description)
 VALUES('testCamp1', "fake_charity", 1000, '10,20','3,3','float1', 500, 10, "empty");
 
 INSERT INTO Campaign (campaign_name, charity, goal_amount, initial_location, destination, 
owner_account, accumulated_donation, time_left, description)
 VALUES('testCamp2', "fake_charity1", 1000, '10,20','3,3','float2', 500, 10, "empty"); 
 
 INSERT INTO Campaign (campaign_name, charity, goal_amount, initial_location, destination, 
owner_account, accumulated_donation, time_left, description)
 VALUES('testCamp3', "fake_charity2", 1000, '10,20','3,3','float4', 500, 10, "empty");  

-- test data for actions table
INSERT INTO Actions(action_id, action_type, associated_camp_name, description, associated_user_name, date, location)
VALUES(1,'init','testCamp1','empty', 'float1', '2016-10-1', '10,20');
INSERT INTO Actions(action_id, action_type, associated_camp_name, description, associated_user_name, date, location)
VALUES(2,'init','testCamp2','empty', 'float2', '2016-10-1', '10,20');
INSERT INTO Actions(action_id, action_type, associated_camp_name, description, associated_user_name, date, location)
VALUES(3,'init','testCamp3','empty', 'float4', '2016-10-1', '10,20');
 
-- test data for camp_user_table
INSERT INTO camp_follower_map(account,campaign_name,date)
VALUE('float3', 'testCamp1', '2016-10-1');
INSERT INTO camp_follower_map(account,campaign_name,date)
VALUE('float3', 'testCamp2', '2016-10-1');
INSERT INTO camp_follower_map(account,campaign_name,date)
VALUE('float3', 'testCamp3', '2016-10-1');
