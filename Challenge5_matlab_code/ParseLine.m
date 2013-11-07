function info = ParseLine(tline)
    info = struct('A1', str2double(tline(1:6)),'A2', str2double(tline(8:13)),'A3',str2double(tline(15:20)));
end