[y,Fs] = audioread('D:\amusic_and_videos\proj_dataset\database\audio\concert.wav'); % y can have + and - amplitudes.
amplitudes = abs(y);  % abs(y) is the amplitudes in an all-positive sense
maxY = max(abs(y)); % maxY is the highest amplitude.