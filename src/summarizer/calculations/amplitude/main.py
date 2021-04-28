# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


# def print_hi(name):
#     # Use a breakpoint in the code line below to debug your script.
#     print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.
#
#
# # Press the green button in the gutter to run the script.
# if __name__ == '__main__':
#     print_hi('PyCharm')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/

# import wave, struct

# wavefile = wave.open('D:\\amusic_and_videos\\proj_dataset\\database\\audio\\concert.wav', 'r')
# amplitur=wavefile.readframes(800)
# print(amplitur)


import wave, struct

wavefile = wave.open('D:\\amusic_and_videos\\proj_dataset\\database\\audio\\concert.wav', 'r')
# amplitur=wavefile.readframes(800)
# print(amplitur)

for i in range(wavefile.getnframes()):
    frame = wavefile.readframes(i)
    print (frame)
