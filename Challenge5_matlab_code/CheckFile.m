function [status, newTrace]= CheckFile(fileName)

% status == 0 means ok;
% status == 1 means wrong and reload
status = 0;
newTrace = [];
fileName='C:\\Users\\wei\\Documents\\MATLAB\\Matlab Code\\xbeedata.txt';
fid = fopen(fileName);
tline = fgetl(fid);
status =1;

%when the java are writing data, tline = -1,so avoid this situation
if(tline~=-1)
    status =0;
    info = ParseLine(tline);
    newTrace = [newTrace, info];
end

fclose(fid);


end