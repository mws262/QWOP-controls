close all; clear;

files = dir("./tmp/*.txt");

col = lines(length(files));
for i = 1:length(files)
    tsNum(i) = str2double(regexp(files(i).name,'\d*','Match'));
    data{i} = importdata(["./tmp/" + files(i).name]);
end
[tsSorted, idx] = sort(tsNum);
data = {data{idx}};

% Nominal trajectory.
fig = figure;
fig.Position = [100, 100, 800, 500];
ax = axes;
hold on;
nominalPl = plot(NaN, NaN, 'LineWidth', 4);
devStartPl = plot(NaN, NaN, 'g', 'LineWidth', 2);
fail1plot = plot(NaN, NaN, 'r', 'LineWidth', 2);
axis([0, 30, -2.5, 3.5]);
avgStep = plot([2.5, 2.5], ax.YLim, 'Color', [162, 36, 229]/255, 'LineWidth', 2);
devPlots = plot(0, zeros(1, 9));

perturbationLabels = {"nominal traj", "deviation start", "first fall", "avg step cycle length", "Q", "W", "O", "P", "QO", "QP", "WO", "WP", "NONE"};
xlabel('Time after perturbation (s)');
ylabel('torso angle (rad)');
legen = legend(perturbationLabels);
legen.Position(2) = 0.58;
legen.Position(1) = 0.77;
ax.XLim = [-0.5, 8];
fig.Color = [1,1,1];

grid on;
vid_name = 'expslowerhead';
vid_writer = VideoWriter([vid_name, '.avi'], 'Uncompressed AVI');
vid_writer.FrameRate = 8;
open(vid_writer);
for j = 1:length(data)
    startT = tsSorted(j) * 0.04;
    devStartPl.XData = [0, 0];
    devStartPl.YData = ax.YLim;
    firstFailIdx = inf;
    
    for i = 1:9
        lastidxplus1 = find(isnan(data{j}(:, i + 2)), 1, 'first');
        if isempty(lastidxplus1)
            lastidxplus1 = length(data{j}(:, i + 2)) + 1;
        end
        if lastidxplus1 < firstFailIdx
            firstFailIdx = lastidxplus1;
        end
        
%         data{j}(lastidxplus1 - 1, 1) - tsSorted(j) * 0.04
        devPlots(i).XData =  data{j}(:,1) - startT;
        devPlots(i).YData = data{j}(:, i + 2);
    end
        fail1plot.XData = [data{j}(firstFailIdx - 1, 1) - startT, data{j}(firstFailIdx - 1, 1) - startT];
        fail1plot.YData = ax.YLim;

            nominalPl.XData = data{end}(:, 1) - startT;
        nominalPl.YData = data{end}(:, 2);

    writeVideo(vid_writer, getframe(fig));

    pause(0.001);
end
close(vid_writer);
                    eval(['!ffmpeg -i ', vid_name, '.avi ', vid_name, '.mp4']);
                    eval(['!rm ', vid_name, '.avi ']); % Remove the temporary avi file.

