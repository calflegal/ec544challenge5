clc;clear;close all;
period = 1; % Pause period
% Store the Last line. Find the Content that is appended. And Plot them
oldLen = 0;
newTrace = [];

stateSet = [];
count = 0; % initialize data count
x1=[];
y1=[];

% case 1: 3 beacons
B=zeros(3,3);
% pick any 3 from 6 nodes, for simplication using Node1, Node2, Node3
    B(1,1)=0; B(1,2)=13;
    B(2,1)=13; B(2,2)=13;
    B(3,1)=13; B(3,2)=0;
figure()

while 1

    [status,newTrace]= CheckFile('C:\\Users\\wei\\Documents\\MATLAB\\Matlab Code\\xbeedata.txt');
    % status == 0 means ok;
    % status == 1 means file open error

    pause(period)

    disp('there is update')
    if(~status)
        % get the distance data in order
        A1 = [newTrace().A1];
        A2 = [newTrace().A2];
        A3 = [newTrace().A3];   
        n=4; % 4 nodes in total
         % The pairwised distance measures are in x, 6*5/2 = 15 elements
         x = [13,18.38,A1,13,A2,A3]';
        % generate the measurement index matrix
         J = zeros(n, n);
         k = 1;
        for i=1:n
         for j = i+1 : n
            J(i,j) = k;
            k = k+1;
            J(j,i) = J(i,j);
         end
        end
    
    

    BN = 3;
    % get the other 3 nodes' position
    P=zeros(1,2);
    i=4;
        for j=1:3
            B(j,3)=abs(x(J(j,4)));
        end
        [P(1,1),P(1,2)] = Trilateration(B, BN)
    %plot the 3 x-bees, and the corner of the origin point
    plot(B(:,1),B(:,2),'bo',0,0,'y*');  
    %keep the plot the same
    hold on
    %plot the location
    plot(P(1,1),P(1,2),'g*')
    
    if((A1<A2)&&(A1<A3))
        hold on
        plot(B(1,1),B(1,2),'ro')
    end
        if((A2<A3)&&(A2<A1))
        hold on
        plot(B(2,1),B(2,2),'ro')
        end
        if((A3<A1)&&(A3<A2))
        hold on
        plot(B(3,1),B(3,2),'ro')
    end
   
    
    %choose the axis 
    axis([-25 25 -25 25])
    pause(1)
    %the next plot will refresh all
    hold off
    end
end