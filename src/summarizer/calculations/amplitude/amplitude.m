[y,Fs] = audioread('D:\amusic_and_videos\proj_dataset\database\audio\concert.wav'); % y can have + and - amplitudes.
amplitudes = abs(y);  % abs(y) is the amplitudes in an all-positive sense
maxY = max(abs(y)); % maxY is the highest amplitude.


%%%%%%%%%%%%%%%%%%%%%%%%%%% Write it into txt version
[y,Fs] = audioread('D:\amusic_and_videos\proj_dataset\database\audio\meridian.wav'); % y can have + and - amplitudes.
amplitudes = abs(y);  % abs(y) is the amplitudes in an all-positive sense
maxY = max(abs(y)); % maxY is the highest amplitude.
%  fileID=fopen('D:\amusic_and_videos\academic performance of USC\576\meridian.txt','w');
%  fprintf(fileID,'%f %f\n',y);
%  fclose(fileID);
%%%%%%%%%%%%%%%%%%%%%%%%%
