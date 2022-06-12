// Engine_MarimbaMxSamples

// Inherit methods from CroneEngine
Engine_MarimbaMxSamples : CroneEngine {

	// <MarimbaMxSamples> 
	var marimbaSamples;
    var marimbaPlay;
    var marimbaPlaying;
	// </MarimbaMxSamples>

    // <mxsamples2>
    var mx;
    // </mxsamples2>

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		// <MarimbaMxSamples> 
        marimbaPlaying=Dictionary.new();
        marimbaSamples=Array.newClear(20);
        marimbaSamples[0]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c1 p.wav");
        marimbaSamples[1]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c1 mp.wav");
        marimbaSamples[2]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c1 mf.wav");
        marimbaSamples[3]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c1 f.wav");
        marimbaSamples[4]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c1 ff.wav");
        marimbaSamples[5]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c2 p.wav");
        marimbaSamples[6]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c2 mp.wav");
        marimbaSamples[7]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c2 mf.wav");
        marimbaSamples[8]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c2 f.wav");
        marimbaSamples[9]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c2 ff.wav");
        marimbaSamples[10]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c3 p.wav");
        marimbaSamples[11]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c3 mp.wav");
        marimbaSamples[12]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c3 mf.wav");
        marimbaSamples[13]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c3 f.wav");
        marimbaSamples[14]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c3 ff.wav");
        marimbaSamples[15]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c4 p.wav");
        marimbaSamples[16]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c4 mp.wav");
        marimbaSamples[17]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c4 mf.wav");
        marimbaSamples[18]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c4 f.wav");
        marimbaSamples[19]=Buffer.read(context.server,"/home/we/dust/code/marimba/data/Marimba - White beater - c4 ff.wav");

		context.server.sync;

        SynthDef("marimbax",{
            arg out=0,reverbOut,reverbSend=0,fade_trig=0,fade_time=0.1,
            velocityMix=0.5,buf1,buf2,t_trig=1,rate=1;
            var snd;
            var sndA,sndA1,sndA2;

            rate=rate*BufRateScale.ir(buf1);

            sndA1=PlayBuf.ar(2,buf1,rate,doneAction:2);
            sndA2=PlayBuf.ar(2,buf2,rate,doneAction:2);
            snd=SelectX.ar(velocityMix,[sndA1,sndA2],0);

            DetectSilence.ar(snd,0.001,doneAction:2);
            snd = snd/10;
            snd=snd*EnvGen.ar(Env.new([1,0],[fade_time]),fade_trig,doneAction:2);
            Out.ar(out,snd);
        }).add;

        marimbaPlay = {
            arg instrument,note,velocity=120;
            var name=instrument.asString++"-"++note.asString;
            var rate=1;
            var availableNotes=[24,36,48,60];
            var sampleStart=availableNotes.indexIn(note);
            var noteDifference=note-availableNotes[sampleStart];
            var ratios=Tuning.et12.ratios;
            var triggered=false;
            var availableVelocities=[-1,128/5,128/5*2,128/5*3,128/5*4,128];
            var velocityClosest=availableVelocities.indexOfGreaterThan(velocity)-1;
            var velocityMix=(velocity-availableVelocities[velocityClosest])/(availableVelocities[velocityClosest+1]-availableVelocities[velocityClosest]);
            // ("velocityClosest"+velocityClosest).postln;
            // ("velocityMix"+velocityMix).postln;

            if (noteDifference<0,{
                rate = rate/2;
                noteDifference=note-(availableNotes[sampleStart]-12);
            });
            rate = rate * ratios[noteDifference];
            sampleStart = sampleStart * 5;
            // ("sampleStart"+sampleStart).postln;

            if (marimbaPlaying.at(name).notNil,{
                if (marimbaPlaying.at(name).isRunning,{
                    marimbaPlaying.at(name).set(\fade_trig,1);
                });
            });
            marimbaPlaying.put(name,Synth("marimbax",[
                \rate,rate,\velocityMix,velocityMix,
                \buf1,marimbaSamples[velocityClosest+sampleStart],
                \buf2,marimbaSamples[1+velocityClosest+sampleStart]
            ]));//.onFree({"freed".postln}));
            NodeWatcher.register(marimbaPlaying.at(name));
        };

		this.addCommand("play","iff", { arg msg;
            marimbaPlay.(msg[1],msg[2],msg[3]);
		});
        
		// </MarimbaMxSamples> 

        // <mxsamples2>
        mx=MxSamples(Server.default,100,0);

        this.addCommand("mx_note_on","sff", { arg msg;
            mx.noteOn(msg[1].asString,msg[2],msg[3]);
        });

        this.addCommand("mx_note_onfx","sffffffffffffff", { arg msg;

            mx.noteOnFX(msg[1].asString,msg[2],msg[3],
                //amp,pan,attack,decay,sustain,release,delaysend,reverbsend,lpf,lpfrq,hpf,hpfrq
                msg[4],
                msg[5],
                msg[6],
                msg[7],
                msg[8],
                msg[9],
                msg[10],
                msg[11],
                msg[12],
                msg[13],
                msg[14],
                msg[15],
            );
        });
        
        this.addCommand("mx_note_off","sf", { arg msg;
            mx.noteOff(msg[1].asString,msg[2]);
        });

        this.addCommand("mx_set","ssf", { arg msg;
            mx.setParam(msg[1].asString,msg[2].asString,msg[3]);
        });

        this.addCommand("mxsamples_sustain", "si", { arg msg;
            mx.setSustain(msg[1].asString,msg[2]==1);
        });

        this.addCommand("mxsamples_sustenuto", "si", { arg msg;
            mx.setSustenuto(msg[1].asString,msg[2]==1);
        });
        // </mxsamples2>

	}

	free {
		// <MarimbaMxSamples> 
		marimbaPlaying.keysValuesDo({ arg key, value; value.free; });
		(0..19).do({ arg i; marimbaSamples[i].free; });
		// </MarimbaMxSamples> 

        // <mxsamples2>
        mx.free;
        // </mxsamples2>
	}
}
